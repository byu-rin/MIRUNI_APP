package com.miruni.feature.calendar.presentation.model

import android.annotation.SuppressLint
import com.miruni.feature.calendar.domain.model.PlanDraft
import com.miruni.feature.calendar.domain.model.PlanPriority
import java.time.LocalDate

@SuppressLint("NewApi")
data class AddScheduleState(
    val title: String = "",
    val dateTimeRange: DateTimeRangeState = DateTimeRangeState(
        startDate = LocalDate.now(),
        endDate = LocalDate.now()
    ),
    val priority: PlanPriority = PlanPriority.MEDIUM,
    val description: String = "",
) {
    fun toDomain(): PlanDraft {
        return PlanDraft(
            title = title,
            description = description,
            startDate = dateTimeRange.startDate.toString(),
            endDate = dateTimeRange.endDate.toString(),
            startTime = dateTimeRange.startTime.toString(),
            endTime = dateTimeRange.endTime.toString(),
            priority = priority
        )
    }
}