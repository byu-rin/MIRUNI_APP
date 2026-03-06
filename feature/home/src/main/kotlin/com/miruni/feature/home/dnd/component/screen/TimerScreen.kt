package com.miruni.feature.home.dnd.component.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.designsystem.MiruniTypography
import com.miruni.feature.home.R
import com.miruni.feature.home.dnd.TimerMode
import com.miruni.feature.home.dnd.component.DndTopBar
import com.miruni.feature.home.dnd.component.InputTimeView
import com.miruni.feature.home.dnd.component.button.RowButton
import com.miruni.feature.home.dnd.component.button.SingleGreenButton
import com.miruni.feature.home.presentation.DndContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    state: DndContract.State,
    timePickerState: TimePickerState,
    onSetTime: (Int, Int) -> Unit,
    onPauseClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onStartClick: () -> Unit,
    navController: NavHostController,
    showSettingTime: Boolean,
    showTopBar: Boolean,
) {
    val showTopBarCloseButton = state.mode != TimerMode.RUNNING

    DndTimerSetContent(
        state = state,
        timePickerState = timePickerState,
        onCloseClick = {
            navController.popBackStack()
        },
        onConfirmClick = {
            onSetTime(timePickerState.hour, timePickerState.minute)
            onStartClick()
            Log.d("DndTimerScreen", "mode = ${state.mode}")

        },
        onPauseClick = onPauseClick,
        onCompleteClick = onCompleteClick,
        showSettingTime = showSettingTime,
        showTopBar = showTopBar,
        showTopBarCloseButton = showTopBarCloseButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DndTimerSetContent(
    state: DndContract.State,
    timePickerState: TimePickerState,
    onCloseClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onPauseClick: () -> Unit,
    onCompleteClick: () -> Unit,
    showSettingTime: Boolean,
    showTopBar: Boolean,
    showTopBarCloseButton: Boolean
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                DndTopBar(
                    onClose = onCloseClick,
                    showCloseButton = showTopBarCloseButton,
//                    showTopBar = showTopBar
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1A24C354)
                ),
                modifier = Modifier.height(65.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("다른 어플 사용을 제한")
                            }
                            append("하고, 할 일에 더 집중해보세요.")
                        },
                        textAlign = TextAlign.Center,
                        style = AppTypography.body_regular_14
                    )
                }
            }
            Spacer(Modifier.height(100.dp))

            Image(
                painter = painterResource(id = R.drawable.miruni_lock),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Spacer(Modifier.height(100.dp))

            when (state.mode) {
                TimerMode.SET -> {
                    if (showSettingTime) {
                        Text(
                            text = "설정 시간 : 60분",
                            modifier = Modifier
                                .background(
                                    color = Gray.gray_400,
                                    shape = RoundedCornerShape(percent = 50)
                                )
                                .padding(start = 60.dp, end = 60.dp, top = 8.dp, bottom = 8.dp),
                            color = Color.White,
                        )
                    }

                    Spacer(Modifier.height(70.dp))

                    InputTimeView(
                        timePickerState = timePickerState,
                        isRunning = false,
                    )

                    Spacer(Modifier.height(70.dp))

                    SingleGreenButton(
                        onClick = onConfirmClick,
                        text = "확인"
                    )
                }

                TimerMode.RUNNING -> {
                    RunningTimerView(
                        state = state,
                        onPauseClick = onPauseClick,
                        onCompleteClick = onCompleteClick
                    )
                }

                TimerMode.PAUSED -> {}
            }
        }
    }
}

@Composable
private fun RunningTimerView(
    state: DndContract.State,
    onPauseClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(top = 50.dp),
            style = MiruniTypography.displayMedium,
            color = MainColor.miruni_green,
            text = "%02d".format(state.hours),
            fontSize = 48.sp
        )
        Spacer(Modifier.width(10.dp))
        Text(
            modifier = Modifier
                .padding(top = 50.dp),
            style = MiruniTypography.displayMedium,
            color = Color.Black,
            text = ":",
            fontSize = 48.sp
        )
        Spacer(Modifier.width(10.dp))
        Text(
            modifier = Modifier
                .padding(top = 50.dp),
            style = MiruniTypography.displayMedium,
            color = MainColor.miruni_green,
            text = "%02d".format(state.minutes),
            fontSize = 48.sp
        )
    }

    Spacer(Modifier.height(40.dp))

    Text(
        text = "미루니와 함께 성장하고 있어요!",
        color = Color.Gray,
        style = AppTypography.body_regular_14
    )

    RowButton(
        text1 = "중지",
        text2 = "완료",
        onClickButton1 = onPauseClick,
        onClickButton2 = onCompleteClick,
        button1Color = Gray.gray_500,
        button2Color = MainColor.miruni_green
    )
}

@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
fun TimerScreenPreview() {
    MiruniTheme {
        TimerScreen(
            state = DndContract.State(),
            timePickerState = rememberTimePickerState(),
            onSetTime = { _, _ -> },
            onPauseClick = {},
            onCompleteClick = {},
            onStartClick = {},
            navController = rememberNavController(),
            showSettingTime = false,
            showTopBar = true,
        )
    }
}