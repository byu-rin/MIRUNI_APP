package com.miruni.feature.home

import androidx.lifecycle.viewModelScope
import com.miruni.core.domain.onboarding.OnboardingKey
import com.miruni.core.domain.onboarding.OnboardingRepository
import com.miruni.core.common.BaseViewModel
import com.miruni.core.result.DataError
import com.miruni.core.result.DataResult
import com.miruni.feature.home.domain.repository.HomeRepository
import com.miruni.feature.home.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val homeRepository: HomeRepository
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    /** State 초기화 */
    override fun setInitialState(): HomeContract.State = HomeContract.State (
        progressRate = 0
    )

    /** 이벤트 핸들링 */
    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.OnBackClick -> setEffect { HomeContract.Effect.PopBack }
            HomeContract.Event.OnAlarmClick -> setEffect { HomeContract.Effect.Navigation.ToAlarms }
            HomeContract.Event.OnAiPlannerClick -> goToAiPlanner()
            HomeContract.Event.OnDndClick -> goToDndMode()
            is HomeContract.Event.OnScheduleClick -> handleScheduleClick(event.scheduleId)
        }
    }

    init {
        loadHome()
    }

    /** 홈 화면 정보 로드 */
    private fun loadHome() {
        viewModelScope.launch {
            setState { copy(isHomeLoading = true) }
            // API 호출
            val userDeferred = async { homeRepository.getHomeUser() }
            val planDeferred = async { homeRepository.getHomePlan() }

            val userResult = userDeferred.await()
            val planResult = planDeferred.await()

            setState { copy(isHomeLoading = false) }

            // 결과 처리
            when {
                userResult is DataResult.Error -> {
                    showErrorMessage(userResult.error)
                }
                planResult is DataResult.Error -> {
                    showErrorMessage(planResult.error)
                }

                userResult is DataResult.Success && planResult is DataResult.Success -> {
                    setState {
                        copy(
                            userInfo = userResult.data.toUiModel(),
                            progressRate = planResult.data.progressRate,
                            schedules = planResult.data.todayPlans?.map { it.toUiModel() } ?: emptyList()
                        )
                    }
                }
            }

        }
    }

    /**
     * 일정 클릭 처리
     */
    private fun handleScheduleClick(scheduleId: Int) {
        val currentSelected = viewState.value.selectedScheduleId

        if (currentSelected == scheduleId) {
            setEffect {
                HomeContract.Effect.Navigation.ToExecution(scheduleId)
            }
        } else {
            setState {
                copy(selectedScheduleId = scheduleId)
            }
        }
    }

    /**
     * 방해금지 모드로 이동.
     * 온보딩인지 / 방해금지모드 메인인지 판단 후 Effect 발생
     */
    private fun goToDndMode() {
        viewModelScope.launch {
            val onboardingCompleted = onboardingRepository
                .isCompleted(OnboardingKey.DND)
                .first()

            if (onboardingCompleted) { // 온보딩 완료 시
                setEffect { HomeContract.Effect.Navigation.ToDnd }
            } else {
                setEffect { HomeContract.Effect.Navigation.ToDndOnboarding }
            }
        }
    }

    /**
     * AI 플래너로 이동.
     * 온보딩인지 / AI 플래너 메인인지 판단 후 Effect 발생
     */
    private fun goToAiPlanner() {
        viewModelScope.launch {
            val onboardingCompleted = onboardingRepository
                .isCompleted(OnboardingKey.AI_PLANNER)
                .first()

            if (onboardingCompleted) { // 온보딩 완료 시
                setEffect { HomeContract.Effect.Navigation.ToAiPlanner }
            } else {
                setEffect { HomeContract.Effect.Navigation.ToAiPlannerOnboarding }
            }
        }
    }

    private fun showErrorMessage(error: DataError?) {
        val message = when(error) {
            is DataError.CustomError -> error.msg
            is DataError.Unknown -> error.errorMessage
            else -> "네트워크 연결을 확인해주세요."
        }

        setEffect { HomeContract.Effect.ShowToast(message) }
    }
}