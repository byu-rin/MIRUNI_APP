package com.miruni.feature.calendar.presentation.model

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("NewApi")
data class DateTimeRangeState(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime = LocalTime.of(10, 0),
    val endTime: LocalTime = LocalTime.of(11, 0),
) {
    fun formatDate(): String {
        val startFormatted = formatLocalDate(startDate)
        val endFormatted = formatLocalDate(endDate)

        return if (startDate == endDate) {
            startFormatted
        } else {
            "$startFormatted ~ $endFormatted"
        }
    }

    //날짜 format
    private fun formatLocalDate(date: LocalDate): String {
        return "${date.year}.${
            date.monthValue.toString().padStart(2, '0')
        }.${
            date.dayOfMonth.toString().padStart(2, '0')
        }"
    }

    //날짜 + 시간
    fun formatDateTime(): String {
        val startFormatted = "${formatLocalDate(startDate)} ${formatLocalTime(startTime)}"
        val endFormatted = "${formatLocalDate(endDate)} ${formatLocalTime(endTime)}"

        // 날짜 및 시간 동일
        return if (startDate == endDate && startTime == endTime) {
            startFormatted
        } else if (startDate == endDate) {
            // 날짜 동일 및 다른 시간
            "${formatLocalDate(startDate)} ${formatLocalTime(startTime)} ~ ${formatLocalTime(endTime)}"
        } else {
            // 다른 날짜
            "$startFormatted ~ $endFormatted"
        }
    }

    //시간 format
    private fun formatLocalTime(time: LocalTime): String {
        val hour = when {
            time.hour == 0 -> 12           // 00시 → 12 오전
            time.hour == 12 -> 12          // 12시 → 12 오후
            time.hour > 12 -> time.hour - 12  // 13~23시 → 1~11 오후
            else -> time.hour              // 1~11시 → 1~11 오전
        }
        val minute = time.minute.toString().padStart(2, '0')
        val amPm = if (time.hour < 12) "오전" else "오후"

        return "$amPm $hour:$minute"
    }
}