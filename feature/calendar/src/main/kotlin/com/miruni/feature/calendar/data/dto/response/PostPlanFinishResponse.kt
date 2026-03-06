package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.FinishPlan
import com.miruni.feature.calendar.domain.model.PlanStatus
import com.miruni.feature.calendar.domain.model.PlanType

data class PostPlanFinishResponse(
    val peanutCount: Int,
    val planType: String,
    val planId: Int,
    val status: String
) {
    fun toDomain(): FinishPlan {
        return FinishPlan(
            peanutCount = peanutCount,
            planType = PlanType.fromServer(planType),
            planId = planId,
            status = PlanStatus.fromServer(status)
        )
    }
}