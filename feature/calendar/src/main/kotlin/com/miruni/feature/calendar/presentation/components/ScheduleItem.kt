package com.miruni.feature.calendar.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.White
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.presentation.model.ScheduleUiModel

@Composable
fun ScheduleItem(
    schedule: ScheduleUiModel,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .background(Gray.gray_300, RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) MainColor.miruni_green else Gray.gray_300,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {

        CircleCheckbox(
            checked = schedule.isCompleted,
            onCheckedChange = onCheckedChange
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 중앙: 제목 + 설명
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = schedule.title,
                style = AppTypography.sub_bold_14,
                color = Black
            )

            if (schedule.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = schedule.description,
                    style = AppTypography.body_regular_12,
                    color = Gray.gray_700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 오른쪽: 시간 + 우선순위
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = schedule.timeRange,
                style = AppTypography.body_regular_12,
                color = Gray.gray_700
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 우선순위 뱃지
            PriorityBadge(priority = schedule.priority)
        }
    }
}

@Composable
fun CircleCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(
                color = if (checked) MainColor.miruni_green else Gray.gray_400,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "완료",
                tint = White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
@Preview
fun ScheduleItemPreview() {
    ScheduleItem(
        schedule = ScheduleUiModel(
            id = "1",
            title = "스케줄 등록",
            description = "스케줄 Content",
            priority = PlanPriority.HIGH
        ),
        onClick = {},
        onCheckedChange = {},
        isSelected = true
    )
}