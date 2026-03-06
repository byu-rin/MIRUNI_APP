package com.miruni.feature.calendar.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.White
import java.time.YearMonth

@SuppressLint("NewApi")
@Composable
fun YearMonthPickerDialog(
    currentYearMonth: YearMonth,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit,
) {
    val years = remember { (2020..2030).toList() }
    val months = remember { (1..12).toList() }

    var selectedYear by remember { mutableIntStateOf(currentYearMonth.year) }
    var selectedMonth by remember { mutableIntStateOf(currentYearMonth.monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(YearMonth.of(selectedYear, selectedMonth))
                    onDismiss()
                }
            ) {
                Text("확인", color = MainColor.miruni_green)
            }
        },
        text = {
            Row(
                modifier = Modifier
                    .height(240.dp)
                    .background(White),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(years) { year ->
                        YearMonthItem(
                            text = "${year}년",
                            isSelected = year == selectedYear,
                            onClick = { selectedYear = year }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(months) { month ->
                        YearMonthItem(
                            text = "${month}월",
                            isSelected = month == selectedMonth,
                            onClick = { selectedMonth = month }
                        )
                    }
                }
            }
        },
        containerColor = White
    )
}

@Composable
fun YearMonthItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .background(
                if (isSelected) MainColor.alpha_10 else androidx.compose.ui.graphics.Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = if (isSelected) AppTypography.sub_bold_14 else AppTypography.body_regular_14,
            color = if (isSelected) MainColor.miruni_green else Black
        )
    }
}