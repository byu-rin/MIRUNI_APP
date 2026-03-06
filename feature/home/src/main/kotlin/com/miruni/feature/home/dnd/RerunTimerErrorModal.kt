package com.miruni.feature.home.dnd

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.HomeRoute
import com.miruni.feature.home.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RerunTimerErrorModal(
    navController: NavHostController
) {
    Dialog(
        onDismissRequest = {},
    ) {
        Surface(
            modifier = Modifier.size(330.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.fluent_warning_filled),
                    contentDescription = "warning",
                    modifier = Modifier
                        .size(100.dp)
                )
                Text(
                    text = "해당 시간에는 이미 일정이 있어요!\n다시 설정해주세요 :)",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center, // 텍스트 자체를 가운데 정렬
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )
                Button(
                    onClick = {
                        navController.navigate(HomeRoute.Home.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(text = "확인", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RerunTimerErrorModalPreview() {
    MiruniTheme {
        RerunTimerErrorModal(
            navController = rememberNavController()
        )
    }
}