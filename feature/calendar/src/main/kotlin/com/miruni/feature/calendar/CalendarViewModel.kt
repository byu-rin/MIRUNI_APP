package com.miruni.feature.calendar

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.miruni.core.common.BaseViewModel
import com.miruni.core.result.DataError
import com.miruni.core.result.DataResult
import com.miruni.feature.calendar.domain.model.DailyPlans
import com.miruni.feature.calendar.domain.model.PlanType
import com.miruni.feature.calendar.domain.repository.CalendarRepository
import com.miruni.feature.calendar.presentation.model.AddScheduleState
import com.miruni.feature.calendar.presentation.model.ScheduleUiModel
import com.miruni.feature.calendar.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : BaseViewModel<CalendarContract.Event, CalendarContract.State, CalendarContract.Effect>() {

    override fun setInitialState() = CalendarContract.State()

    private val _selectedDateFlow = MutableStateFlow(setInitialState().selectedDate)
    val selectedDateFlow: StateFlow<LocalDate> = _selectedDateFlow.asStateFlow()

    override fun handleEvents(event: CalendarContract.Event) {
        when (event) {
            // AI 플래닝하러 가기
            CalendarContract.Event.AiPlannerClicked -> setEffect { CalendarContract.Effect.NavigateToAiPlanner }

            // 날짜 타일 클릭
            is CalendarContract.Event.DayClicked -> setSelectedDay(event.date)

            // 일정 생성
            CalendarContract.Event.ChangeIsPlanCreationOpened -> changeIsPlanCreationOpened()
            is CalendarContract.Event.SubmitPlan -> createPlan(event.state)

            // 일정 클릭
            is CalendarContract.Event.PlanClicked -> selectPlan(event.plan)
            is CalendarContract.Event.OpenPlanSheet -> openPlanSheet(event.planType, event.planId)
            CalendarContract.Event.ClosePlanSheet -> setState { copy(isPlanSheetOpened = false) }
            // 일정 완료 설정
            is CalendarContract.Event.PlanChecked -> finishPlan(event.plan)
            // - 전체 보기
            is CalendarContract.Event.ShowDetailClicked -> setEffect { CalendarContract.Effect.NavigateToScheduleTable(event.plan.id.toInt()) }
            // - 수정하기
            is CalendarContract.Event.PlanEditClicked -> setEditMode(event.plan)
            is CalendarContract.Event.SubmitEditedPlan -> submitEditedPlan(event.editedPlan, event.addScheduleState)
            // - 삭제하기
            CalendarContract.Event.PlanDeleteClicked -> deletePlan()
        }
    }

    init {
        observeDailyPlans()
        refreshDailyPlans()
    }

    @Suppress("NewApi")
    fun onMonthChanged(yearMonth: YearMonth) {
        viewModelScope.launch {
            setState { copy(currentMonth = yearMonth) }

            val result = calendarRepository.getMonthlyPlanCount(yearMonth.year, yearMonth.monthValue)

            when (result) {
                is DataResult.Success -> {
                    val resultMap = result.data.associate { dayInfo ->
                        LocalDate.parse(dayInfo.date) to dayInfo.unfinishedPlanCount
                    }

                    setState { copy(unfinishedCountByDate = resultMap) }
                }

                is DataResult.Error -> {
                    showErrorMessage(result.error)
                }
            }
        }
    }

    /**
     * 오늘의 일정 구독
     * - 선택 날짜 변동시 DailyPlan 반영
     */
    private fun observeDailyPlans() {
        viewModelScope.launch {
            selectedDateFlow
                .flatMapLatest { date ->
                    calendarRepository.observeDailyPlans(date)
                        .catch { e ->
                            Log.d("CalendarViewModel", "observeDailyPlans: $e")
                            emit(DailyPlans(unfinishedPlan = emptyList(), finishedPlan = emptyList()))
                        }
                }
                .collect { dailyPlans ->
                    Log.d("PLAN_DEBUG", "8. ViewModel collect됨: ${dailyPlans.unfinishedPlan.size} / ${dailyPlans.finishedPlan.size}")

                    val unfinished = dailyPlans.unfinishedPlan.mapNotNull { plan ->
                        runCatching { plan.toUiModel() }.getOrNull()
                    }
                    val finished = dailyPlans.finishedPlan.mapNotNull { plan ->
                        runCatching { plan.toUiModel() }.getOrNull()
                    }

                    setState {
                        copy(
                            unfinishedDailyPlans = unfinished,
                            finishedDailyPlans = finished,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun refreshDailyPlans() {
        viewModelScope.launch {
            val today = viewState.value.selectedDate
            calendarRepository.refreshDailyPlans(today)
        }
    }

    /** 날짜 선택 처리 */
    private fun setSelectedDay(date: LocalDate) {
        setState { copy(selectedDate = date) }
        _selectedDateFlow.value = date

        viewModelScope.launch { calendarRepository.refreshDailyPlans(date) }
    }
    /** 일정 완료 처리 */
    private fun finishPlan(plan: ScheduleUiModel) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val planResult = calendarRepository.refreshPlan(
                planType = plan.planType,
                planId = plan.id.toInt()
            )
            Log.d("Refresh/Get Plan", "7. ViewModel Result: $planResult")

            when (planResult) {
                is DataResult.Success -> {

                    val planData = planResult.data
                    val planUiModel = planData.toUiModel()

                    setState {
                        copy(
                            // selectedPlan의 필드 업데이트
                            selectedPlan = planUiModel,
                            isLoading = false
                        )
                    }
                    Log.d("Refresh/Get Plan", "8. ViewModel plan: $planData")
                    Log.d("Refresh/Get Plan", "9. ViewModel planUiModel: $planUiModel")

                    val hour = planUiModel.expectedTime.toInt() / 60
                    val minute = planUiModel.expectedTime.toInt() % 60

                    val result = calendarRepository.finishPlan(
                        planType = planUiModel.planType,
                        planId = planUiModel.id.toInt(),
                        expectedTime = "$hour:$minute",
                        date = viewState.value.selectedDate
                    )

                    when (result) {
                        is DataResult.Success -> {
                            calendarRepository.refreshDailyPlans(viewState.value.selectedDate)
                        }
                        is DataResult.Error -> setEffect {
                            CalendarContract.Effect.ShowToast("완료 처리 실패. ${result.error}")
                        }
                    }
                }
                is DataResult.Error -> {
                    setState { copy(isLoading = false) }
                    showErrorMessage(planResult.error)
                }
            }
        }
    }
    /** 일정 생성 처리 */
    private fun createPlan(state: AddScheduleState) {
        Log.d("PLAN_DEBUG", "2. ViewModel.createPlan 시작")
        setState { copy(isAddScheduleSheetOpened = false) }

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val draft = state.toDomain()
            val result = calendarRepository.createPlan(draft)
            Log.d("PLAN_DEBUG", "3. Repository.createPlan 반환: $result")

            when (result) {
                is DataResult.Success -> {
                    setEffect { CalendarContract.Effect.ShowToast("일정이 생성되었습니다.") }
                }
                is DataResult.Error -> {
                    setEffect { CalendarContract.Effect.ShowToast("일정 생성 실패. ${result.error}") }
                }
            }
        }
    }
    /** 일정 등록 바텀 시트 여닫기 */
    private fun changeIsPlanCreationOpened() {
        val isAddScheduleSheetOpened = viewState.value.isAddScheduleSheetOpened
        setState { copy(isAddScheduleSheetOpened = !isAddScheduleSheetOpened) }
    }
    /** 일정 선택 */
    private fun selectPlan(plan: ScheduleUiModel) {
        setState { copy(selectedPlan = plan) }
    }
    /** 동일 일정 2번 클릭 시 일정 설명 바텀 시트 출력 */
    private fun openPlanSheet(
        planType: PlanType,
        planId: Int
    ) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = calendarRepository.refreshPlan(planId, planType)
            Log.d("Refresh/Get Plan", "7. ViewModel Result: $result")

            when (result) {
                is DataResult.Success -> {

                    val plan = result.data
                    val planUiModel = plan.toUiModel()

                    setState {
                        copy(
                            // selectedPlan의 필드 업데이트
                            selectedPlan = planUiModel,
                            isLoading = false
                        )
                    }
                    Log.d("Refresh/Get Plan", "8. ViewModel plan: $plan")
                    Log.d("Refresh/Get Plan", "9. ViewModel planUiModel: $planUiModel")

                }
                is DataResult.Error -> {
                    setState { copy(isLoading = false) }
                    showErrorMessage(result.error)
                }
            }
        }

        setState { copy(isPlanSheetOpened = !isPlanSheetOpened) }
    }

    private fun setEditMode(plan: ScheduleUiModel) {
        setState {
            copy(
                isPlanSheetOpened = false,
                isAddScheduleSheetOpened = true,
                selectedPlan = plan,
                editingPlan = plan
            )
        }
    }
    private fun submitEditedPlan(
        editedPlan: ScheduleUiModel,
        addScheduleState: AddScheduleState
    ) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = calendarRepository.editPlan(
                basicPlanId = editedPlan.id.toInt(),
                draft = addScheduleState.toDomain()
            )

            when (result) {
                is DataResult.Success -> {
                    calendarRepository.refreshDailyPlans(addScheduleState.dateTimeRange.startDate)

                    setState {
                        copy(
                           isLoading = false,
                            isAddScheduleSheetOpened = false,
                            editingPlan = null
                        )
                    }

                    setEffect { CalendarContract.Effect.ShowToast("일정이 수정되었습니다.") }
                }

                is DataResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { CalendarContract.Effect.ShowToast("일정 수정 실패. ${result.error}") }
                }
            }
        }
    }

    private fun deletePlan() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val state = viewState.value

            val result = calendarRepository.deletePlan(
                planId = state.selectedPlan!!.id.toInt(),
                date = viewState.value.selectedDate
            )

            Log.d("Delete Plan", "5. ViewModel Result: $result")

            when (result) {
                is DataResult.Success -> {
                    calendarRepository.refreshDailyPlans(state.selectedDate)

                    setState { copy(isLoading = false) }
                    setEffect { CalendarContract.Effect.ShowToast("일정이 삭제되었습니다.") }
                }
                is DataResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { CalendarContract.Effect.ShowToast("일정 삭제 실패. ${result.error}") }
                }
            }
        }
    }

    private fun showErrorMessage(error: DataError?) {
        val message = when(error) {
            is DataError.CustomError -> error.msg
            is DataError.Unknown -> error.errorMessage
            else -> "네트워크 연결을 확인해주세요."
        }

        setEffect { CalendarContract.Effect.ShowToast(message) }
    }
}