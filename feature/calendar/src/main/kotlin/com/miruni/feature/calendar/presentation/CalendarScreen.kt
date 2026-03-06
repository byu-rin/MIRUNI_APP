package com.miruni.feature.calendar.presentation

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.designsystem.White
import com.miruni.core.navigation.MiruniRoute
import com.miruni.feature.calendar.CalendarContract
import com.miruni.feature.calendar.CalendarViewModel
import com.miruni.feature.calendar.R
import com.miruni.feature.calendar.domain.model.PlanType
import com.miruni.feature.calendar.presentation.components.AddScheduleBottomSheet
import com.miruni.feature.calendar.presentation.components.AiPlanningButton
import com.miruni.feature.calendar.presentation.components.ScheduleBottomSheet
import com.miruni.feature.calendar.presentation.components.ScheduleItem
import com.miruni.feature.calendar.presentation.components.YearMonthPickerDialog
import com.miruni.feature.calendar.presentation.model.AddScheduleState
import com.miruni.feature.calendar.presentation.model.ScheduleUiModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarRoute(
    navController: NavHostController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CalendarContract.Effect.NavigateToAiPlanner -> navController.navigate(MiruniRoute.AiPlannerMain.route)
                is CalendarContract.Effect.NavigateToScheduleTable -> {
                    navController.navigate("aiPlanner") {
                        launchSingleTop = true
                    }
                    navController.navigate(
                        "${MiruniRoute.AiPlannerSchedule.route}?from=MAIN&planId=${effect.planId}"
                    )
                }
                is CalendarContract.Effect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    CalendarScreen(
        state = state,
        onMonthChanged = { yearMonth ->
            viewModel.onMonthChanged(yearMonth)
        },
        onAiPlanningClicked = {
            viewModel.setEvent(CalendarContract.Event.AiPlannerClicked)
        },
        onDayClicked = { date ->
            viewModel.setEvent(CalendarContract.Event.DayClicked(date))
        },
        onPlanClicked = { plan ->
            viewModel.setEvent(CalendarContract.Event.PlanClicked(plan))
        },
        onPlanDetailClicked = { plan ->
            viewModel.setEvent(CalendarContract.Event.ShowDetailClicked(plan))
        },
        onPlanEditClicked = {
            viewModel.setEvent(CalendarContract.Event.PlanEditClicked(state.selectedPlan!!))
        },
        onCheckBoxClicked = { plan ->
            viewModel.setEvent(CalendarContract.Event.PlanChecked(plan = plan))
        },
        onAddConfirmClicked = { addScheduleState ->
            viewModel.setEvent(CalendarContract.Event.SubmitPlan(addScheduleState))
        },
        onEditConfirmClicked = { editingPlan, addScheduleState ->
            viewModel.setEvent(
                CalendarContract.Event.SubmitEditedPlan(
                    editingPlan,
                    addScheduleState
                )
            )
        },
        onPlanDeleteClicked = {
            viewModel.setEvent(CalendarContract.Event.PlanDeleteClicked)
        },
        changeIsPlanCreationSheetOpened = {
            viewModel.setEvent(CalendarContract.Event.ChangeIsPlanCreationOpened)
        },
        openPlanSheet = { planType, planId ->
            viewModel.setEvent(CalendarContract.Event.OpenPlanSheet(planType, planId))
        },
        closePlanSheet = {
            viewModel.setEvent(CalendarContract.Event.ClosePlanSheet)
        }
    )
}

@SuppressLint("NewApi")
@Composable
fun CalendarScreen(
    state: CalendarContract.State,
    modifier: Modifier = Modifier,
    onMonthChanged: (YearMonth) -> Unit,
    onAiPlanningClicked: () -> Unit,
    onDayClicked: (java.time.LocalDate) -> Unit = {},
    onPlanClicked: (ScheduleUiModel) -> Unit,
    onPlanDetailClicked: (ScheduleUiModel) -> Unit,
    onPlanEditClicked: () -> Unit,
    onPlanDeleteClicked: () -> Unit,
    onCheckBoxClicked: (ScheduleUiModel) -> Unit,
    onAddConfirmClicked: (AddScheduleState) -> Unit,
    onEditConfirmClicked: (ScheduleUiModel, AddScheduleState) -> Unit,
    changeIsPlanCreationSheetOpened: () -> Unit,
    openPlanSheet: (PlanType, Int) -> Unit,
    closePlanSheet: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showYearMonthPicker by remember { mutableStateOf(false) }

    val startMonth = remember { YearMonth.now().minusMonths(24) }
    val endMonth = remember { YearMonth.now().plusMonths(24) }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = state.currentMonth,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collect { yearMonth ->
                onMonthChanged(yearMonth)
            }
    }

    LaunchedEffect(state.unfinishedDailyPlans) {
        Log.d("Calendar", "List changed: ${state.unfinishedDailyPlans.size}")
    }

    if (showYearMonthPicker) {
        YearMonthPickerDialog(
            currentYearMonth = state.currentMonth,
            onDismiss = { showYearMonthPicker = false },
            onConfirm = {
                onMonthChanged(it)
            }
        )
    }

    /** 일정 등록 바텀 시트 */
    if (state.isAddScheduleSheetOpened) {
        AddScheduleBottomSheet(
            isLoading = state.isLoading,
            selectedDate = state.selectedDate,
            editingPlan = state.editingPlan,
            onConfirm = { addState, editingPlan ->
                if (editingPlan == null) {
                    onAddConfirmClicked(addState)
                } else {
                    onEditConfirmClicked(editingPlan, addState)
                }
            },
            onDismiss = { changeIsPlanCreationSheetOpened() }
        )
    }

    /** 일정 설명 바텀 시트 */
    if (state.isPlanSheetOpened) {
        state.selectedPlan?.let { plan ->
            Log.d("Refresh/Get Plan", "10. selectedPlan: $plan")
            ScheduleBottomSheet(
                title = plan.title,
                description = plan.description,
                date = "${state.selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))} ${plan.startTime} - ${plan.endTime}",
                priority = plan.priority,
                onDismiss = { closePlanSheet() },
                onClickPlanDetail = { onPlanDetailClicked(plan) },
                onEdit = {
                    closePlanSheet()
                    onPlanEditClicked()
                },
                onDelete = {
                    closePlanSheet()
                    onPlanDeleteClicked()
                }
            )
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            CalendarHeader(
                currentMonth = state.currentMonth,
                onYearMonthClick = {
                    showYearMonthPicker = true
                },
                onPrevMonthClick = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                        )
                    }
                },
                onNextMonthClick = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                        )
                    }
                },
                onAddCalendarClick = { changeIsPlanCreationSheetOpened() }
            )
        }
        item {
            HorizontalCalendar(
                state = calendarState,
                monthHeader = {
                    WeekDayHeader()
                },
                dayContent = { day ->
                    val count = state.unfinishedCountByDate[day.date] ?: 0

                    DayCell(
                        day = day,
                        unfinishedCount = count,
                        isSelected = state.selectedDate == day.date,
                        isToday = state.today == day.date,
                        onClick = { onDayClicked(day.date) }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
        }

        item {
            AiPlanningButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { onAiPlanningClicked() }
            )
            Spacer(modifier = Modifier.height(25.dp))
        }

        item {
            val unfinished = state.unfinishedDailyPlans
            val finished = state.finishedDailyPlans

            when {
                unfinished.isEmpty() && finished.isEmpty() -> {
                    Text(
                        text = "오늘은 일정이 없어요",
                        style = AppTypography.sub_semibold_12,
                        modifier = Modifier.padding(start = 16.dp, top = 10.dp)
                    )
                }

                unfinished.isNotEmpty() -> {
                    unfinished.forEach { plan ->
                        ScheduleItem(
                            schedule = plan,
                            onClick = {
                                if (state.selectedPlan == plan) {
                                    openPlanSheet(plan.planType, plan.id.toInt())
                                } else {
                                    onPlanClicked(plan)
                                }
                            },
                            isSelected = state.selectedPlan == plan,
                            onCheckedChange = { onCheckBoxClicked(plan) }
                        )
                    }

                    if (finished.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = "완료",
                            style = AppTypography.sub_bold_14,
                            modifier = Modifier.padding(start = 16.dp, top = 9.dp, bottom = 5.dp)
                        )
                        finished.forEach { plan ->
                            ScheduleItem(
                                schedule = plan,
                                onClick = {
                                    if (state.selectedPlan == plan) {
                                        openPlanSheet(plan.planType, plan.id.toInt())
                                    } else {
                                        onPlanClicked(plan)
                                    }
                                },
                                isSelected = state.selectedPlan == plan,
                                onCheckedChange = { onCheckBoxClicked(plan) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    modifier: Modifier = Modifier,
    onYearMonthClick: () -> Unit,
    onPrevMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onAddCalendarClick: () -> Unit,
) {
    val currentMonthLabel = remember(currentMonth) {
        "${currentMonth.year}년 ${currentMonth.monthValue}월"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentMonthLabel,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable { onYearMonthClick() }
        )

        Row {
            IconButton(onClick = onPrevMonthClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 달"
                )
            }
            IconButton(onClick = onNextMonthClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 달"
                )
            }
            IconButton(onClick = onAddCalendarClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.btn_plus),
                    contentDescription = "일정 추가"
                )
            }
        }
    }
}

@Composable
fun WeekDayHeader(
    modifier: Modifier = Modifier,
) {
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = Gray.gray_500
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun DayCell(
    day: CalendarDay,
    unfinishedCount: Int,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val textColor = when {
        isToday -> MainColor.miruni_green
        isSelected -> Color(0xFFFBBC05)
        !isCurrentMonth -> Gray.gray_500
        isCurrentMonth -> Black
        else -> White
    }
    val indicatorColor = when (unfinishedCount) {
        in 1..5 -> Color(0xFFCBCBCB)
        in 6..10 -> Color(0xFF8FDA8D)
        in 11..15 -> Color(0xFF1EC718)
        in 16..Int.MAX_VALUE -> Color(0xFF0D7C0A)
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .clickable(
                enabled = isCurrentMonth,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 날짜 숫자 + 원형 배경
        Box(
            modifier = Modifier
                .size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text( // 날짜
                text = day.date.dayOfMonth.toString(),
                style = AppTypography.sub_medium_14,
                color = textColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        if (unfinishedCount > 0) {
            Canvas(
                modifier = Modifier.size(8.dp)
            ) {
                drawCircle(
                    color = indicatorColor,
                    radius = size.minDimension / 2
                )
            }
        }
    }
}

@Preview
@Composable
fun CalendarScreenPreview() {
    MiruniTheme {
//        CalendarScreen(
//            state = CalendarContract.State(),
//            onMonthChanged = {},
//            onAiPlanningClicked = {},
//            onPlanClicked = {},
//            onCheckBoxClicked = {},
//            onAddConfirmClicked = {},
//            changeIsPlanCreationSheetOpened = {},
//            openPlanSheet = { planType, planId ->
//            },
//            closePlanSheet = {},
//            onDayClicked = {},
//            onPlanDetailClicked = {},
//            onPlanEditClicked = {},
//            onEditConfirmClicked = { _, _ -> },
//            onPlanDeleteClicked = {}
//        )

        DayCell(
            day = CalendarDay(
                date = LocalDate.of(2026, 3, 1),
                position = DayPosition.MonthDate
            ),
            unfinishedCount = 10,
            modifier = Modifier,
            isSelected = false,
            isToday = false,
            onClick = {}
        )
    }
}