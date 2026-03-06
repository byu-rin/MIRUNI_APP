package com.miruni.feature.calendar.domain.model

data class DailyPlans(
    val unfinishedPlan: List<Plan>,
    val finishedPlan: List<Plan>
)
