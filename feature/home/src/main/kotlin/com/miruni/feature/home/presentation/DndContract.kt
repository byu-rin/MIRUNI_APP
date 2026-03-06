package com.miruni.feature.home.presentation

import com.miruni.core.common.ViewEvent
import com.miruni.core.common.ViewSideEffect
import com.miruni.core.common.ViewState
import com.miruni.feature.home.dnd.TimerMode

class DndContract {
    sealed class Event : ViewEvent {
        object CompleteOnboarding : Event()

        data class SetTime(val hour: Int, val minute: Int) : Event()
        object Start : Event()
        object Pause : Event()
        object End : Event()
        object Resume : Event()
    }

    data class State(
        val remainingMinute: Int = 0, // 남아있는 분
        val isRunning: Boolean = false, // 타이머가 현재 실행 중인지 여부
        val isDone: Boolean = true, // 타이머가 끝났는지 확인
        val mode: TimerMode = TimerMode.SET
    ) : ViewState {
        // 파생 상태 (Derived State)
        // State를 직접 바꾸지 않고 계산으로만 사용
        val hours: Int get() = remainingMinute / 60
        val minutes: Int get() = remainingMinute % 60
    }

    sealed class Effect : ViewSideEffect {
        object TimeFinished : Effect()
        object NavigateToPause : Effect()
        object NavigateToEarlyEnd : Effect()
        object NavigateToHome : Effect()
    }
}

class DndModalContract {

    sealed class ModalEffect : ViewSideEffect {
        object OpenRerunTimerErrorModal : ModalEffect()
        object OpenRerunTimerSettingModal : ModalEffect()
        object Close : ModalEffect()
    }
}