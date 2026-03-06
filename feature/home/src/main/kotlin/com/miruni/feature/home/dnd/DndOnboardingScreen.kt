package com.miruni.feature.home.dnd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miruni.core.designsystem.MiruniTheme
import com.miruni.feature.home.R

@Composable
fun DndOnboardingScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF24C354)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // 말풍선 카드
        ShadowCustomCard(
            modifier = Modifier
                .size(width = 264.dp, height = 88.dp)
                .padding(start = 48.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "다른 앱을 사용을 제한하고\n할 일에 더 집중해보세요!",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 캐릭터 이미지
        Image(
            painter = painterResource(R.drawable.miruni_lock),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(190.dp)
        )

        Image(
            painter = painterResource(R.drawable.miruni_shadow),
            contentDescription = null,
            modifier = Modifier
                .width(107.dp)
                .height(23.dp)
        )

        Spacer(modifier = Modifier.weight(2f))

        // 시작하기 버튼 (하단 우측)
        StartButton(
            onClick = onStartClick,
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun StartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            text = "시작하기",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun ShadowCustomCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(9.dp),
    containerColor: Color = Color(0x40FBFBFB),
    shadowColor: Color = Color(0x33000000),
    offsetX: Dp = 2.dp,
    offsetY: Dp = 2.dp,
    blurRadius: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current

    // 그림자 레이어
    Box(
        modifier = modifier
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = shadowColor.toArgb()
                        setShadowLayer(
                            blurRadius.toPx(),
                            offsetX.toPx(),
                            offsetY.toPx(),
                            shadowColor.toArgb()
                        )
                    }

                    canvas.save()
                    canvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        with(density) { 9.dp.toPx() },
                        with(density) { 9.dp.toPx() },
                        Paint().apply { asFrameworkPaint().set(paint) }
                    )
                    canvas.restore()
                }
            }
    ) {
        // 실제 카드
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                content = content
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    MiruniTheme {
        DndOnboardingScreen(
            onStartClick = {}
        )
    }
}