package com.miruni.feature.calendar.presentation.model

import com.miruni.feature.calendar.domain.model.Plan
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.domain.model.PlanType

data class ScheduleUiModel(
    val planType: PlanType = PlanType.BASIC,
    val id: String,
    val title: String,
    val description: String = "",
    val startTime: String? = "",
    val endTime: String? = "",
    val priority: PlanPriority,
    val isCompleted: Boolean = false,
    val expectedTime: String = "0"
) {
    val timeRange: String
        get() = "$startTime - $endTime"
}

fun Plan.toUiModel(): ScheduleUiModel {
    return ScheduleUiModel(
        id = planId.toString(),
        title = title,
        description = description ?: "",
        startTime = startTime,
        endTime = endTime,
        priority = priority,
        isCompleted = isDone,
        expectedTime = (expectedDuration ?: "").toString()
    )
}