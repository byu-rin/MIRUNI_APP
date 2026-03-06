package com.miruni.feature.home.dnd

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.miruni.core.common.BaseViewModel
import com.miruni.core.domain.onboarding.OnboardingKey
import com.miruni.core.domain.onboarding.OnboardingRepository
import com.miruni.feature.home.presentation.DndContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TimerMode {
    SET,      // 시간 설정 화면
    RUNNING,  // 타이머 실행 화면
    PAUSED    // 일시정지 화면
}

@HiltViewModel
class DndTimerViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) :
    BaseViewModel<DndContract.Event, DndContract.State, DndContract.Effect>() {

    // 타이머 코루틴 Job
    private var timerJob: Job? = null

    override fun setInitialState() = DndContract.State()

    override fun handleEvents(event: DndContract.Event) {
        when (event) {
            DndContract.Event.CompleteOnboarding -> completeOnboarding()

            is DndContract.Event.SetTime -> setTime(event.hour, event.minute)
            DndContract.Event.Start -> start()
            DndContract.Event.Pause -> pause()
            DndContract.Event.End -> end()
            DndContract.Event.Resume -> resume()
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.completeOnboarding(OnboardingKey.DND)
        }

    }

    /**
     * 사용자가 시간을 설정했을 때 호출
     * - remainingMinute 계산
     * - 상태를 SET → RUNNING 으로 전이시키기 위해 start() 호출
     */
    private fun setTime(hour: Int, minute: Int) {
        val total = hour * 60 + minute

        Log.d("DndTimerViewModel", "setTime: $total")

        setState {
            copy(
                remainingMinute = total,
                isRunning = false,
                isDone = false,
                mode = TimerMode.SET
            )
        }

        Log.d("DndTimerViewModel", "start 함수 호출")

        // 상태 전이
        start()
    }

    /**
     * 타이머 실행 "의도(Intent)" 처리
     *
     * - 실행 가능한 상태인지 검사
     * - State 를 RUNNING 으로 변경
     * - 실제 시간 감소는 startTimer() 에 위임
     */
    private fun start() {
        val current = viewState.value

        Log.d(
            "DndTimer",
            "▶ start() called, remaining=${current.remainingMinute}"
        )

        // 이미 실행 중이거나 시간이 없으면 무시
        if (current.isRunning || current.remainingMinute <= 0) return

        // 실행상태로 변경
        setState {
            copy(
                isRunning = true,
                isDone = false,
                mode = TimerMode.RUNNING
            )
        }

        // 실제 타이머 로직 시작 (Side Effect)
        startTimer()
    }

    /**
     * 실제 시간 감소를 담당하는 함수
     * - 코루틴 기반 Side Effect
     * - State 변경은 setState 를 통해서만 수행
     */
    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            Log.d("DndTimerViewModel", "start timer")

            // 타이머가 살아 있고, 시간이 남아 있는 동안 반복
            while (isActive && viewState.value.remainingMinute > 0) {
                delay(1_000) // test
//                delay(60_000) // realtime

                val before = viewState.value.remainingMinute

                // 1분 감소
                setState {
                    copy(remainingMinute = remainingMinute - 1)
                }

                val after = viewState.value.remainingMinute

                Log.d("DndTimerViewModel", "1분 감소 : $before -> $after")
            }

            // 시간이 끝났을 때 Effect 발생
            if (viewState.value.remainingMinute <= 0) {
                setEffect { DndContract.Effect.TimeFinished }
            }
        }
    }

    /**
     * 타이머 일시정지
     * - 코루틴 중단
     * - 상태를 PAUSED 로 전이
     * - Pause 화면으로 이동 Effect 발생
     */
    private fun pause() {
        timerJob?.cancel() // 코루틴 중단

        // 실행 상태 해제
        setState {
            copy(
                isRunning = false,
                mode = TimerMode.PAUSED
            )
        }

        setEffect { DndContract.Effect.NavigateToPause }
    }

    /**
     * 타이머 일시정지
     * - 코루틴 중단
     * - 상태를 PAUSED 로 전이
     * - EarlyEnd 화면으로 이동 Effect 발생
     */
    private fun end() {
        timerJob?.cancel() // 코루틴 중단

        // 실행 상태 해제
        setState {
            copy(
                isRunning = false,
                mode = TimerMode.PAUSED
            )
        }

        setEffect { DndContract.Effect.NavigateToEarlyEnd }
    }

    /**
     * 일시정지 상태에서 재개
     * - 상태를 RUNNING 으로 변경
     * - 타이머 코루틴 재시작
     */
    private fun resume() {
        Log.d("DndTimerViewModel", "resume() 함수 호출")
        if (viewState.value.remainingMinute <= 0) return

        // 실행 상태 재개
        setState {
            copy(
                isRunning = true,
                mode = TimerMode.RUNNING
            )
        }

        startTimer()
    }
}