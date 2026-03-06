package com.miruni.feature.mypage.info

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.core.navigation.MyPageRoute
import com.miruni.feature.mypage.component.MyPageTopBar

private const val TAG = "InformationScreen"

@Composable
fun InformationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color(0xFFF6F5F6),
        topBar = {
            MyPageTopBar(
                text = "문의 및 정보",
                onBackClick = {
                    Log.d(TAG, "Back button clicked")
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Card(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                Color.White
            )
        ) {
            Column(
                modifier = modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = "앱 정보",
                    style = AppTypography.header_bold_16
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "앱 버전: v1.0.0",
                    style = AppTypography.body_regular_12
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "개발팀: Team Miruni",
                    style = AppTypography.body_regular_12
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Gray.gray_300,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "문의 및 피드백",
                    style = AppTypography.header_bold_16
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        modifier = modifier
                            .size(90.dp, 30.dp)
                            .testTag("writeButton"),
                        onClick = {
                            Log.d(TAG, "작성하기 button clicked")
                            navController.navigate(MyPageRoute.MyPageWriteFeedback.route)
                        },
                        border = BorderStroke(1.dp, Color(0xFF1B67FF)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "작성하기",
                            style = AppTypography.body_regular_12,
                            color = Color(0xFF1B67FF)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        modifier = modifier
                            .size(90.dp, 30.dp)
                            .testTag("historyButton"),
                        onClick = {
                            Log.d(TAG, "내역보기 button clicked")
                            navController.navigate(MyPageRoute.MyPageShowFeedbackHistory.route)
                        },
                        border = BorderStroke(1.dp, Color(0xFF1B67FF)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "내역보기",
                            style = AppTypography.body_regular_12,
                            color = Color(0xFF1B67FF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                HorizontalDivider(
                        thickness = 1.dp,
                color = Gray.gray_300,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "정책 및 약관",
                    style = AppTypography.header_bold_16
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    AnnotatedString.fromHtml(
                        """<a href="https://www.google.com">개인정보 처리방침 보기</a>""",
                        linkStyles = TextLinkStyles(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = Color(0xFF1B67FF)
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    AnnotatedString.fromHtml(
                        """<a href="https://www.google.com">서비스 이용약관 보기</a>""",
                        linkStyles = TextLinkStyles(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = Color(0xFF1B67FF)
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Gray.gray_300,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "저작권",
                    style = AppTypography.header_bold_16
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ⓒ 2025 Team Miruni Inc. All rights reserved.",
                    style = AppTypography.body_regular_12
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationScreenPreview() {
    MiruniTheme {
        InformationScreen(
            navController = rememberNavController()
        )
    }
}
