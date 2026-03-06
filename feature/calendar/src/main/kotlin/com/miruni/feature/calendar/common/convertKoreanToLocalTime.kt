package com.miruni.feature.calendar.common

import android.annotation.SuppressLint
import java.time.LocalTime

@SuppressLint("NewApi")
fun convertKoreanToLocalTime(
    timeString: String,
    default: LocalTime = LocalTime.of(10, 0)
): LocalTime {
    if (timeString.isNullOrBlank()) return default

    return try {
        val trimmed = timeString.trim()
        val regex = Regex("""(오전|오후)\s*(\d{1,2}):(\d{2})""")

        val match = regex.find(trimmed) ?: return default

        val amPm = match.groupValues[1]
        val hour = match.groupValues[2].toInt()
        val minute = match.groupValues[3].toInt()

        val hour24 = when {
            amPm == "오전" && hour == 12 -> 0
            amPm == "오후" && hour != 12 -> hour + 12
            else -> hour
        }

        return LocalTime.of(hour24, minute)
    } catch (e: Exception) {
        default
    }
}