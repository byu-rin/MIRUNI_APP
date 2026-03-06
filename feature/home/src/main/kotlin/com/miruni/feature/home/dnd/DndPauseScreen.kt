package com.miruni.feature.home.dnd

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.HomeRoute
import com.miruni.feature.home.dnd.component.screen.PauseOrEarlyEndScreen
import com.miruni.feature.home.presentation.DndContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DndPauseScreen(
    navController: NavHostController,
    viewModel: DndTimerViewModel = hiltViewModel(),
    hour: Int,
    minute: Int
) {
    var showRerunTimerSettingModal by remember { mutableStateOf(false) }
    var showErrorModal by remember { mutableStateOf(false) }

    var currentHour by remember { mutableStateOf(hour) }
    var currentMinute by remember { mutableStateOf(minute) }

    BackHandler {
        viewModel.setEvent(DndContract.Event.Resume)
        navController.popBackStack()
    }

    PauseOrEarlyEndScreen(
        hour = currentHour,
        minute = currentMinute,
        title = "정말 중지하시겠어요?",
        subDescription = "지금 멈추면\n땅콩을 n개 받을 수 있어요!",
        navController = navController,
        onClickButton1 = {
            viewModel.setEvent(DndContract.Event.Resume)
            navController.popBackStack()
        },
        onClickButton2 = {
            // TODO: 모달 연결
        }
    )

    // setting modal
    if (showRerunTimerSettingModal) {
        RerunTimerSettingModal(
            onGoSetting = {
                showRerunTimerSettingModal = false
                navController.navigate(HomeRoute.Home.route)
            },
            onClose = {

            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DndPauseScreenPreview() {
    MiruniTheme {
        DndPauseScreen(
            navController = rememberNavController(),
            hour = 0,
            minute = 0,
        )
    }
}