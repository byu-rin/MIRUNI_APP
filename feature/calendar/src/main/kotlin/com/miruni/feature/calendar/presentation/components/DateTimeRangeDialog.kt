package com.miruni.feature.calendar.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.miruni.core.designsystem.AppTypography
import com.miruni.core.designsystem.Black
import com.miruni.core.designsystem.Gray
import com.miruni.core.designsystem.MainColor
import com.miruni.core.designsystem.White
import com.miruni.feature.calendar.presentation.WeekDayHeader
import com.miruni.feature.calendar.presentation.model.DateTimeRangeState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@SuppressLint("NewApi")
@Composable
fun DateTimeRangeDialog(
    initialRange: DateTimeRangeState?,
    isSingleDateMode: Boolean = false,
    onConfirm: (DateTimeRangeState) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()

    // 선택된 날짜들
    var startDate by remember { mutableStateOf(initialRange?.startDate ?: today) }
    var endDate by remember { mutableStateOf(initialRange?.endDate ?: today) }

    // 선택된 시간들
    var startTime by remember { mutableStateOf(initialRange?.endTime ?: LocalTime.of(10, 30)) }
    var endTime by remember { mutableStateOf(initialRange?.endTime ?: LocalTime.of(11, 30)) }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }

    val calendarState = rememberCalendarState(
        startMonth = YearMonth.now().minusMonths(12),
        endMonth = YearMonth.now().plusMonths(12),
        firstVisibleMonth = YearMonth.from(startDate),
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // 캘린더 헤더
                CalendarDialogHeader(
                    currentMonth = calendarState.firstVisibleMonth.yearMonth,
                    onPrevMonthClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                            )
                        }
                    },
                    onNextMonthClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    state = calendarState,
                    monthHeader = {
                        WeekDayHeader()
                    },
                    dayContent = { day ->
                        RangeDayCell(
                            day = day,
                            startDate = startDate,
                            endDate = endDate,
                            onClick = {
                                if (isSingleDateMode) {
                                    startDate = day.date
                                    endDate = day.date
                                } else {
                                    handleDateClick(
                                        isSingleDateMode = isSingleDateMode,
                                        clickedDate = day.date,
                                        currentStart = startDate,
                                        currentEnd = endDate,
                                        onRangeChanged = { start, end ->
                                            startDate = start
                                            endDate = end
                                        }
                                    )
                                }
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 시간 선택
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeDropdown(
                        modifier = Modifier.weight(1f),
                        selectedTime = startTime,
                        onTimeSelected = { startTime = it }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    TimeDropdown(
                        modifier = Modifier.weight(1f),
                        selectedTime = endTime,
                        onTimeSelected = { endTime = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 완료 버튼
                Button(
                    onClick = {
                        onConfirm(DateTimeRangeState(startDate, endDate, startTime, endTime))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor.miruni_green
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "완료",
                        style = AppTypography.button_semibold_16,
                        color = White
                    )
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarDialogHeader(
    currentMonth: YearMonth,
    onPrevMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onPrevMonthClick()
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 달"
            )
        }

        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            style = AppTypography.header_bold_16
        )

        IconButton(
            onClick = {
                onNextMonthClick()
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 달"
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun RangeDayCell(
    day: CalendarDay,
    startDate: LocalDate,
    endDate: LocalDate,
    onClick: () -> Unit,
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val date = day.date

    val isStartDate = date == startDate
    val isEndDate = date == endDate
    val isSelected = isStartDate || isEndDate
    val isSingleDate = startDate == endDate
    val isInRange = date.isAfter(startDate) && date.isBefore(endDate)
    val isToday = date == LocalDate.now()

    // 텍스트 색상
    val textColor = when {
        !isCurrentMonth -> Gray.gray_400
        isSelected -> White
        isToday -> MainColor.miruni_green
        else -> Black
    }

    // 범위 배경 Shape
    val rangeBackgroundShape = when {
        isSingleDate -> RoundedCornerShape(10.dp)
        isStartDate -> RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
        isEndDate -> RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
        else -> RectangleShape
    }

    // 범위 배경색
    val rangeBackgroundColor = when {
        isSingleDate -> Color.Transparent
        isStartDate || isEndDate || isInRange -> MainColor.alpha_10
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(rangeBackgroundColor, rangeBackgroundShape)
            .clickable(
                enabled = isCurrentMonth,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // 선택된 날짜 원형 배경
        if (isSelected && isCurrentMonth) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MainColor.miruni_green,
                        rangeBackgroundShape
                    )
            )
        }

        Text(
            text = date.dayOfMonth.toString(),
            style = AppTypography.sub_medium_14,
            color = textColor
        )
    }
}

@SuppressLint("NewApi")
fun handleDateClick(
    isSingleDateMode: Boolean,
    clickedDate: LocalDate,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    onRangeChanged: (LocalDate, LocalDate) -> Unit,
) {
    if (isSingleDateMode) { // 수정 모드 - 단일 날짜 선택
        onRangeChanged(clickedDate, clickedDate)
        return
    }

    when {
        // 같은 날짜 2번 클릭 = 단일 날짜 유지
        currentStart == currentEnd && clickedDate == currentStart -> {
            // 유지
        }
        // 이미 선택된 시작/종료일 클릭 = 단일 날짜로
        clickedDate == currentStart || clickedDate == currentEnd -> {
            onRangeChanged(clickedDate, clickedDate)
        }
        // 단일 날짜 선택 상태에서 다른 날짜 클릭 = 범위 설정
        currentStart == currentEnd -> {
            if (clickedDate.isBefore(currentStart)) {
                onRangeChanged(clickedDate, currentStart)
            } else {
                onRangeChanged(currentStart, clickedDate)
            }
        }
        // 범위가 있는 상태에서 클릭 = 새 범위 시작
        else -> {
            onRangeChanged(clickedDate, clickedDate)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun TimeDropdown(
    modifier: Modifier = Modifier,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    //30분 단위
    val timeOptions = remember {
        buildList {
            for (hour in 0..23) {
                for (minute in listOf(0, 30)) {
                    add(LocalTime.of(hour, minute))
                }
            }
        }
    }

    Box(modifier = modifier) {
        // 선택된 시간 표시
        OutlinedTextField(
            value = formatTime(selectedTime),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Gray.gray_700
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Gray.gray_400,
                disabledContainerColor = Color.White,
                disabledTextColor = Black
            ),
            textStyle = AppTypography.body_regular_14
        )

        // 시간 선택 Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp),
            containerColor = Gray.gray_300,
            tonalElevation = 0.dp
        ) {
            timeOptions.forEach { time ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = formatTime(time),
                            color = if (time == selectedTime) MainColor.miruni_green else Black,
                            fontWeight = if (time == selectedTime) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}

@SuppressLint("NewApi")
fun formatTime(time: LocalTime): String {
    val hour = when {
        time.hour == 0 -> 12           // 00시 → 12 오전
        time.hour == 12 -> 12          // 12시 → 12 오후
        time.hour > 12 -> time.hour - 12  // 13~23시 → 1~11 오후
        else -> time.hour              // 1~11시 → 1~11 오전
    }
    val minute = time.minute.toString().padStart(2, '0')
    val amPm = if (time.hour < 12) "오전" else "오후"

    return "$hour:$minute $amPm"
}