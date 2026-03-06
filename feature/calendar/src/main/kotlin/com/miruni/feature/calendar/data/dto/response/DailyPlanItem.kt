package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.Plan
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.domain.model.PlanType

data class DailyPlanItem(
    val planType: String,
    val planId: Int,
    val title: String,
    val subTitle: String,
    val startTime: String, // 오전/오후 HH:mm
    val endTime: String, // 오전/오후 HH:mm
    val priority: String,
    val isDone: Boolean
) {
    fun toDomain(): Plan {
        return Plan(
            planId = planId,
            planType = PlanType.fromServer(planType),
            title = title,
            subTitle = subTitle,
            description = null,
            startTime = startTime,
            endTime = endTime,
            priority = PlanPriority.fromServer(priority),
            isDone = isDone
        )
    }
}