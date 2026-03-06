package com.miruni.feature.calendar.domain.model

data class BasicPlan(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val startDateTime: String,
    val endDateTime: String,
    val expectedDuration: Int,
    val status: PlanStatus,
    val priority: PlanPriority
)
