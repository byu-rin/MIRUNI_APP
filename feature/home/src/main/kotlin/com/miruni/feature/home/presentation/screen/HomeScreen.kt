package com.miruni.feature.home.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.HomeRoute
import com.miruni.core.navigation.MiruniRoute
import com.miruni.feature.home.component.TodayScheduleItem
import com.miruni.core.common.convertBold
import com.miruni.feature.home.HomeContract
import com.miruni.feature.home.HomeViewModel
import com.miruni.feature.home.R
import com.miruni.feature.home.component.LinearProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.viewState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                HomeContract.Effect.Navigation.ToAiPlannerOnboarding -> navController.navigate(MiruniRoute.AiPlannerOnboarding.route) // AI 플래너 온보딩

                HomeContract.Effect.Navigation.ToAiPlanner -> navController.navigate(MiruniRoute.AiPlannerMain.route) // AI 플래너

                HomeContract.Effect.Navigation.ToAlarms -> navController.navigate(HomeRoute.AlarmLogs.route) // 알람 기록

                HomeContract.Effect.Navigation.ToDndOnboarding -> navController.navigate(HomeRoute.HomeDndOnboarding.route) // 방해금지 모드 온보딩

                HomeContract.Effect.Navigation.ToDnd -> navController.navigate(HomeRoute.HomeDndTimerSetting.route) // 방해금지 모드

                is HomeContract.Effect.Navigation.ToExecution -> navController.navigate(HomeRoute.RunScheduleTimerSetting.route) // 일정 실행

                is HomeContract.Effect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show() // 토스트 출력

                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = MainColor.miruni_green,
        topBar = {
            HeaderRow(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                onClickAlarm = { viewModel.setEvent(HomeContract.Event.OnAlarmClick) }
            )
        }
    ) { innerPadding ->
        HomeContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            onClickAiPlanner = { viewModel.setEvent(HomeContract.Event.OnAiPlannerClick) },
            onClickDnd = { viewModel.setEvent(HomeContract.Event.OnDndClick) },
            onClickSchedule = { viewModel.setEvent(HomeContract.Event.OnScheduleClick(it)) }
        )
    }
}

@Composable
fun HomeContent(
    state: HomeContract.State,
    modifier: Modifier = Modifier,
    onClickAiPlanner: () -> Unit,
    onClickDnd: () -> Unit,
    onClickSchedule: (Int) -> Unit
) {
    Column (
        modifier = modifier.fillMaxSize(),
    ) {
        TopSection(
            state = state,
            modifier = Modifier.wrapContentHeight(),
            onClickAiPlanner = onClickAiPlanner,
            onClickDnd = onClickDnd
        )

        BottomSection(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            onClickSchedule = onClickSchedule
        )
    }
}

@Composable
fun TopSection(
    state: HomeContract.State,
    modifier: Modifier,
    onClickAiPlanner: () -> Unit,
    onClickDnd: () -> Unit
) {
    Box (modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DescriptionSection(state = state)
            ProgressBarSection(state = state)
            ButtonSection(
                onClickAiPlanner = onClickAiPlanner,
                onClickDnd = onClickDnd
            )
        }
    }
}

@Composable
fun BottomSection(
    state: HomeContract.State,
    modifier: Modifier,
    onClickSchedule: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
    ) {
        Text(
            text = "오늘의 일정",
            style = AppTypography.PretendardTextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color.Black,
            modifier = Modifier.padding(start = 21.dp, top = 27.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.schedules?.size ?: 0) { index ->
                val schedule = state.schedules!![index]
                TodayScheduleItem(
                    item = schedule,
                    isSelected = state.selectedScheduleId == schedule.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 17.dp),
                    onClick = { onClickSchedule(schedule.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

/**
 * 헤더
 */
@Composable
fun HeaderRow(
    state: HomeContract.State,
    modifier: Modifier = Modifier,
    onClickAlarm: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )

        Text(
            text = "MIRUNI",
            fontSize = 18.sp,
            style = AppTypography.AlexandriaTextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                letterSpacing = 0f.em
            ),
            modifier = Modifier
                .padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.peanut),
            contentDescription = "peanut"
        )
        Text(
            text = state.userInfo?.peanutCount?.toString() ?: "0",
            style = AppTypography.header_bold_16,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.bell),
            contentDescription = "bell",
            modifier = Modifier.clickable { onClickAlarm() }
        )
    }
}

/**
 * 메인 설명 영역
 */
@Composable
fun DescriptionSection(
    state: HomeContract.State,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp)
            .padding(bottom = 26.dp)
    ) {
        Text(
            text = convertBold("'${state.userInfo?.nickname ?: "익명의 미루니"}'님,\n오늘은 더 나은 하루가 될거예요.\n'미루니가 함께해요!'"),
            style = AppTypography.PretendardTextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeightRatio = 1.23f
            ),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp)
        )

        MiruniIcon(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd),
            videoResId = R.raw.miruni_jump
        )
    }
}

/**
 * 미루니 영상 재생 아이콘
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MiruniIcon(
    modifier: Modifier,
    videoResId: Int
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = RawResourceDataSource.buildRawResourceUri(videoResId)
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false
            volume = 0f
            repeatMode = Player.REPEAT_MODE_OFF // 한 번만 재생
        }
    }

    // ExoPlayer 정리
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier.clickable(
            interactionSource = remember{ MutableInteractionSource() },
            indication = null
        ) {
            // 항상 처음부터 재생
            exoPlayer.seekTo(0)
            exoPlayer.play()
        }
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * 진행률 박스
 */
@Composable
fun ProgressBarSection(
    state: HomeContract.State,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "진행률",
                color = Color.Black,
                style = AppTypography.sub_bold_14
            )
            Text(
                text = "오늘 목표의 " + (state.progressRate * 100) + "%를 달성했어요!",
                color = Gray.gray_500,
                style = AppTypography.description_regular_9
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressBar(
                progress = state.progressRate.toFloat(),
                modifier = Modifier
                    .fillMaxWidth(),
                height = 12.dp,
                progressColor = MainColor.miruni_green,
                backgroundColor = Color(0xFFE5E7EB)
            )
        }
    }
}

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    onClickAiPlanner: () -> Unit,
    onClickDnd: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(126.dp)
                .background(color = Color(0xFFB9E8C6), shape = RoundedCornerShape(10.dp))
                .padding(start = 25.dp, end = 15.dp, top = 15.dp, bottom = 15.dp)
                .clickable { onClickAiPlanner() }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable { onClickAiPlanner() },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.miruni_pencil),
                    contentDescription = "AI Planner",
                    modifier = Modifier
                        .width(65.dp)
                        .height(52.dp)
                )
                Image(
                    painter = painterResource(R.drawable.miruni_shadow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(30.dp)
                        .height(5.dp)
                        .offset(x = (-10).dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "AI 플래너 바로가기",
                style = AppTypography.PretendardTextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = Gray.gray_700,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(126.dp)
                .background(color = Color(0xFFB9E8C6), shape = RoundedCornerShape(10.dp))
                .padding(start = 25.dp, end = 15.dp, top = 15.dp, bottom = 15.dp)
                .clickable { onClickDnd() }
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.miruni_lock),
                    contentDescription = "DND",
                    modifier = Modifier
                        .width(65.dp)
                        .height(52.dp)
                )
                Image(
                    painter = painterResource(R.drawable.miruni_shadow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(30.dp)
                        .height(5.dp)
                        .offset(x = (-10).dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "방해금지 모드 바로가기",
                style = AppTypography.PretendardTextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = Gray.gray_700,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun HomeScreenPreview() {
    MiruniTheme {
        HomeScreen(
            navController = rememberNavController()
        )
    }
}