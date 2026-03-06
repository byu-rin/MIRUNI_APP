package com.miruni.feature.home.dnd

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.HomeRoute
import com.miruni.feature.home.dnd.component.screen.PauseOrEarlyEndScreen
import com.miruni.feature.home.presentation.DndContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DndEarlyEndScreen(
    navController: NavController,
    viewModel: DndTimerViewModel = hiltViewModel(),
    hour: Int,
    minute: Int,
) {

    var currentHour by remember { mutableStateOf(hour) }
    var currentMinute by remember { mutableStateOf(minute) }

    PauseOrEarlyEndScreen(
        hour = currentHour,
        minute = currentMinute,
        title = "벌써 다 끝내셨어요?",
        subDescription = "지금 멈추면\n땅콩을 n개 받을 수 있어요!",
        navController = navController,
        onClickButton1 = { // 취소버튼
            viewModel.setEvent(DndContract.Event.Resume)
            navController.popBackStack()
        },
        onClickButton2 = { // 확인버튼
            navController.navigate(
                HomeRoute.HomeDndComplete.createRoute(
                    hour = currentHour,
                    minute = currentMinute
                )
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DndEarlyEndScreenPreview() {
    MiruniTheme {
        DndEarlyEndScreen(
            navController = rememberNavController(),
            hour = 0,
            minute = 0,
        )
    }
}