package com.miruni.feature.home.runSchedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.designsystem.MiruniTypography
import com.miruni.core.navigation.HomeRoute.RunScheduleTimerSetting
import com.miruni.feature.home.R
import com.miruni.feature.home.dnd.DndTimerViewModel
import com.miruni.feature.home.dnd.component.button.RowButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDndModeScreen(
    navController: NavHostController,
    viewModel: DndTimerViewModel = hiltViewModel()
) {

    DndTimerSetContent(
        onRejectClick = {
            navController.popBackStack()
        },
        onAcceptClick = {
            navController.navigate(RunScheduleTimerSetting.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DndTimerSetContent(
    onRejectClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.miruni_basic),
            contentDescription = null,
            modifier = Modifier.size(195.dp)
        )

        Spacer(Modifier.height(60.dp))

        Text(
            text = "방해금지모드를 사용하시겠어요?",
            style = MiruniTypography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "실행 화면을 벗어나면, 계속 알림을 보내서\n다시 집중할 수 있게 도와드려요!",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(Modifier.height(80.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RowButton(
                text1 = "아니요",
                text2= "예",
                onClickButton1 = onRejectClick,
                onClickButton2 = onAcceptClick,
                button1Color = Gray.gray_500,
                button2Color = MainColor.miruni_green
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectDndModeScreenPreview() {
    MiruniTheme {
        SelectDndModeScreen(
            navController = rememberNavController(),
        )
    }
}