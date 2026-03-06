package com.miruni.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.MainColor
import com.miruni.feature.calendar.domain.model.PlanPriority

@Composable
fun PriorityBadge(
    priority: PlanPriority,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor, text) = when (priority) {
        PlanPriority.HIGH -> Triple(MainColor.alpha_10, MainColor.miruni_green, "상")
        PlanPriority.MEDIUM -> Triple(MainColor.alpha_10, MainColor.miruni_green, "중")
        PlanPriority.LOW -> Triple(MainColor.alpha_10, MainColor.miruni_green, "하")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.sub_semibold_12,
            color = textColor
        )
    }
}