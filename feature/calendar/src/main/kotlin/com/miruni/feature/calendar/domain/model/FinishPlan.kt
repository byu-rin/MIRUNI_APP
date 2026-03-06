package com.miruni.feature.calendar.domain.model

data class FinishPlan(
    val peanutCount: Int,
    val planType: PlanType,
    val planId: Int,
    val status: PlanStatus
)
