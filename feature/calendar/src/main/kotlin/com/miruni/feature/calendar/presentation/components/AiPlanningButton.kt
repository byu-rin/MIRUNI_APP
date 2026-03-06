package com.miruni.feature.calendar.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miruni.core.common.convertBold
import com.miruni.core.designsystem.AppTypography
import com.miruni.feature.calendar.R

@Composable
fun AiPlanningButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .background(color = Color(0xFFEAF9EE), shape = RoundedCornerShape(9.dp))
            .border(width = (0.9).dp, color = Color(0xFF1EC718), shape = RoundedCornerShape(9.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = convertBold("'AI 플래닝' 하러 가기"),
            style = AppTypography.sub_medium_14,
            color = Color(0xFF282828),
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Image(
            painter = painterResource(R.drawable.right_arrow),
            contentDescription = "AI 플래닝 하러 가기",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Preview
@Composable
fun PreviewAiPlanningButton() {
    AiPlanningButton()
}