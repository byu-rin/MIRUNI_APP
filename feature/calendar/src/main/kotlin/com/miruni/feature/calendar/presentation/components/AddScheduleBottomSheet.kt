package com.miruni.feature.calendar.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.feature.calendar.common.MiruniButton
import com.miruni.feature.calendar.common.MiruniTextField
import com.miruni.feature.calendar.common.convertKoreanToLocalTime
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.presentation.model.AddScheduleState
import com.miruni.feature.calendar.presentation.model.DateTimeRangeState
import com.miruni.feature.calendar.presentation.model.ScheduleUiModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleBottomSheet(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    selectedDate: LocalDate,
    editingPlan: ScheduleUiModel?,
    onConfirm: (AddScheduleState, ScheduleUiModel?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember(editingPlan) { mutableStateOf(editingPlan?.title ?: "") }
    var priority by remember(editingPlan) { mutableStateOf(editingPlan?.priority ?: PlanPriority.MEDIUM) }
    var description by remember(editingPlan) { mutableStateOf(editingPlan?.description ?: "") }
    var dateTimeRange by remember(editingPlan) {
        mutableStateOf(
            DateTimeRangeState(
                startDate = selectedDate,
                endDate = selectedDate,
                startTime = convertKoreanToLocalTime(editingPlan?.startTime.toString()),
                endTime = convertKoreanToLocalTime(editingPlan?.endTime.toString())
            )
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DateTimeRangeDialog(
            initialRange = dateTimeRange,
            isSingleDateMode = editingPlan != null,
            onConfirm = {
                showDatePicker = false
                dateTimeRange = if (editingPlan != null) {
                    it.copy(endDate = it.startDate)
                } else it
            },
            onDismiss = { showDatePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .imePadding()
            ) {
                Text(
                    text = "일정 등록하기",
                    style = AppTypography.sub_bold_14,
                    color = Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                InputField(
                    label = "제목",
                    value = title,
                    onValueChange = { title = it },
                )

                Spacer(modifier = Modifier.height(10.dp))

                DropdownField(
                    label = "우선순위",
                    onPrioritySelected = { priority = it },
                    selectedOption = priority
                )

                Spacer(modifier = Modifier.height(10.dp))

                DateField(
                    label = "날짜 및 시간",
                    dateTimeRange = dateTimeRange,
                    onClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(10.dp))

                InputField(
                    label = "설명",
                    value = description,
                    onValueChange = { description = it },
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(50.dp))

                MiruniButton.Single(
                    text = if (isLoading) "등록중..." else "완료",
                    onClick = {
                        onConfirm(
                            AddScheduleState(
                                title = title,
                                dateTimeRange = dateTimeRange,
                                priority = priority,
                                description = description
                            ),
                            editingPlan
                        )
                    },
                    enabled = title.isNotBlank() && !isLoading
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1,
) {
    Column {
        Text(
            text = label,
            style = AppTypography.body_regular_14,
            color = Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        MiruniTextField.InputText(
            value = value,
            onValueChange = onValueChange,
            maxLines = maxLines,
            singleLine = singleLine
        )
    }
}

@Composable
fun DropdownField(
    label: String,
    modifier: Modifier = Modifier,
    onPrioritySelected: (PlanPriority) -> Unit,
    selectedOption: PlanPriority
) {
    Column {
        Text(
            text = label,
            style = AppTypography.body_regular_14,
            color = Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        val density = LocalDensity.current
        var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

        var expanded by remember { mutableStateOf(false) }
        val options = remember { PlanPriority.entries }

        Box(modifier = modifier
            .fillMaxWidth()
            .height(41.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (expanded) MainColor.miruni_green else Gray.gray_400,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { expanded = true }
            .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()

            ) {
                BasicTextField(
                    value = selectedOption.ui,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    textStyle = AppTypography.PretendardTextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        lineHeightRatio = 1f
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size
                        }
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Gray.gray_700
                )
            }

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .width(
                        with(density) { textFieldSize.width.toDp() }
                    )
                    .heightIn(max = 250.dp),
                onDismissRequest = { expanded = false },
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                options.forEachIndexed { index, priority ->
                    val isSelected = priority == selectedOption

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = priority.ui,
                                    style = AppTypography.PretendardTextStyle(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        lineHeightRatio = 0.625f
                                    ).copy(
                                        color = if (isSelected) Color.White else Black
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) MainColor.miruni_green
                                else Color.White
                            ),
                        onClick = {
                            onPrioritySelected(priority)
                            expanded = false
                        }
                    )

                    if (index != options.lastIndex) {
                        HorizontalDivider(color = Gray.gray_300)
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun DateField(
    label: String,
    dateTimeRange: DateTimeRangeState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column {
        Text(
            text = label,
            style = AppTypography.body_regular_14,
            color = Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(41.dp)
                .border(1.dp, Gray.gray_500, RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateTimeRange.formatDateTime(),
                    style = AppTypography.body_regular_14,
                    color = Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gray.gray_700
                )
            }
        }
    }
}