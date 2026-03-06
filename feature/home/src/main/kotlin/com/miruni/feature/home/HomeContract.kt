package com.miruni.feature.home

import com.miruni.core.common.ViewEvent
import com.miruni.core.common.ViewSideEffect
import com.miruni.core.common.ViewState
import com.miruni.feature.home.presentation.model.AlarmLogItemUiModel
import com.miruni.feature.home.presentation.model.TodayPlanUiModel
import com.miruni.feature.home.presentation.model.UserInfoUiModel

class HomeContract {
    sealed class Event : ViewEvent {
        object OnBackClick: Event()// 뒤로 가기
        object OnAlarmClick : Event() // 알람 기록 클릭
        object OnAiPlannerClick : Event() // AI 플래너 바로가기 클릭
        object OnDndClick : Event() // 방해금지 모드 바로가기 클릭
        data class OnScheduleClick(val scheduleId: Int) : Event() // 일정 클릭 이벤트
    }

    data class State(
        val schedules: List<TodayPlanUiModel>? = emptyList(), // 오늘의 일정 리스트
        val progressRate: Int, // 진행률
        val userInfo: UserInfoUiModel? = null, // 유저 정보
        val selectedScheduleId: Int? = null, // 선택한 일정 ID
        val isHomeLoading: Boolean = false, // 홈 화면 로딩

        val alarmLogs: List<AlarmLogItemUiModel>? = emptyList()
    ) : ViewState

    sealed class Effect: ViewSideEffect {
        /** Navigation */
        sealed class Navigation : Effect() {
            object ToAlarms : Navigation()
            object ToAiPlanner : Navigation()
            object ToAiPlannerOnboarding : Navigation()
            object ToDnd : Navigation()
            object ToDndOnboarding : Navigation()
            data class ToExecution(val scheduleId: Int) : Navigation()
        }
        object PopBack : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}