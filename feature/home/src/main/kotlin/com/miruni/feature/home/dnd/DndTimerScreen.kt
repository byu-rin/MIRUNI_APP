package com.miruni.feature.home.dnd

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.HomeRoute
import com.miruni.feature.home.dnd.component.screen.TimerScreen
import com.miruni.feature.home.presentation.DndContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DndTimerScreen(
    navController: NavHostController,
    viewModel: DndTimerViewModel = hiltViewModel()
) {
    // viewmodel 이 노출한 단일 UI state
    val state by viewModel.viewState.collectAsState()

    Log.d(
        "DndTimerUI",
        "Recompose | remaining=${state.remainingMinute} " +
                "(${state.hours}:${"%02d".format(state.minutes)})"
    )

    // UI 전용 상태 (TimePickerState 내부 상태)
    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = state.hours,
        initialMinute = state.minutes
    )

    Log.d(
        "DndTimerSet", "Composable Recomposition."
    )

    // 일회성 sideEffect 수신 (Navigation)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DndContract.Effect.TimeFinished -> {
                    navController.navigate(
                        HomeRoute.HomeDndComplete.createRoute(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                    ) {
                        // 타이머 화면 스택에서 제거
                        popUpTo(HomeRoute.HomeDndTimerSetting.route) {
                            inclusive = true
                        }
                    }
                }

                DndContract.Effect.NavigateToPause -> {
                    navController.navigate(
                        HomeRoute.HomeDndPause.createRoute(
                            hour = state.hours,
                            minute = state.minutes
                        )
                    )
                }

                DndContract.Effect.NavigateToEarlyEnd -> {
                    navController.navigate(
                        HomeRoute.HomeDndEarlyEnd.createRoute(
                            hour = state.hours,
                            minute = state.minutes
                        )
                    )
                }

                DndContract.Effect.NavigateToHome -> {
                    navController.navigate(
                        HomeRoute.Home.route
                    )
                }
            }
        }
    }

    TimerScreen(
        state = state,
        timePickerState = timePickerState,
        onSetTime = { hour, minute ->
            viewModel.setEvent(
                DndContract.Event.SetTime(
                    hour = hour,
                    minute = minute
                )
            )
        },
        onPauseClick = {
            viewModel.setEvent(
                DndContract.Event.Pause
            )
        },
        onCompleteClick = {
            viewModel.setEvent(
                DndContract.Event.End
            )
        },
        onStartClick = {
            viewModel.setEvent(
                DndContract.Event.Start
            )
        },
        navController = navController,
        showSettingTime = false,
        showTopBar = true,
    )
}

@Preview(showBackground = true)
@Composable
fun DndTimerScreenPreview() {
    MiruniTheme {
        DndTimerScreen(
            navController = rememberNavController(),
        )
    }
}