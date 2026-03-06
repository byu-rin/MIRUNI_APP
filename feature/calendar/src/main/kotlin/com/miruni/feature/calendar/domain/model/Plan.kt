package com.miruni.feature.calendar.domain.model

data class Plan(
    val userId: Int? = null,
    val planId: Int,
    val planType: PlanType,
    val title: String,
    val subTitle: String? = "",
    val description: String? = null,
    val startDateTime: String? = null, // yyyy-MM-dd'T'HH:mm:ss
    val endDateTime: String? = null, // yyyy-MM-dd'T'HH:mm:ss
    val startTime: String? = null, // 오전/오후 HH:mm
    val endTime: String? = null, // 오전/오후 HH:mm
    val expectedDuration: Int? = null,
    val priority: PlanPriority,
    val status: PlanStatus? = null,
    val isDone: Boolean = false
)