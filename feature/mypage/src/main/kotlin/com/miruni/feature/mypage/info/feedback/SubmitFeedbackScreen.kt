package com.miruni.feature.mypage.info.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.designsystem.Yellow
import com.miruni.core.navigation.MyPageRoute
import com.miruni.feature.mypage.R
import com.miruni.feature.mypage.component.MyPageBottomBar

@Composable
fun SubmitFeedbackScreen(
    navController: NavHostController,
) {
    Scaffold(
        containerColor = Color(0xFF24C354),
        bottomBar = {
            MyPageBottomBar(
                canConfirm = true,
                btnText = "확인",
                contentColor = Color.Black,
                containerColor = Color.White,
                onConfirmClick = {
                    navController.navigate(MyPageRoute.MyPageInfo.route) {
                        popUpTo(MyPageRoute.MyPageInfo.route) { inclusive = true }
                    }
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF24C354)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.spring_mypage),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 600.dp, height = 400.dp)
                    .offset(x = 100.dp, y = (-300).dp)
            )

            Image(
                painter = painterResource(id = R.drawable.spring_mypage),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 600.dp, height = 400.dp)
                    .offset(x = (-100).dp, y = 300.dp)
                    .rotate(150f)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "소중한 의견\n감사합니다.",
                    style = AppTypography.header_bold_20,
                    color = Yellow.yellow,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "미루니가 꼼꼼히 읽고\n빠르게 답변드릴게요!",
                    style = AppTypography.body_regular_14,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubmitFeedbackScreenPreview() {
    MiruniTheme {
        SubmitFeedbackScreen(
            navController = rememberNavController()
        )
    }
}