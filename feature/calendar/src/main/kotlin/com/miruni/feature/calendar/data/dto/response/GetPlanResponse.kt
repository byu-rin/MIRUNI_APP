package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.Plan
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.domain.model.PlanType

data class GetPlanResponse(
    val planType: String,
    val planId: Int,
    val title: String,
    val subTitle: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val priority: String
) {
    fun toDomain(): Plan {
        return Plan(
            planId = planId,
            planType = PlanType.fromServer(planType),
            title = title,
            subTitle = subTitle,
            description = description,
            startTime = startTime,
            endTime = endTime,
            priority = PlanPriority.fromServer(priority)
        )
    }
}