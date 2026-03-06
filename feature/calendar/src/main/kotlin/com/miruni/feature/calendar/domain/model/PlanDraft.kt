package com.miruni.feature.calendar.domain.model

data class PlanDraft(
    val title: String,
    val description: String,
    val startDate: String, // 시작 일자 혹은 일정의 해당 날짜
    val endDate: String?, // 없으면 단일 일정. 있으면 범위 일정
    val startTime: String,
    val endTime: String,
    val priority: PlanPriority
)