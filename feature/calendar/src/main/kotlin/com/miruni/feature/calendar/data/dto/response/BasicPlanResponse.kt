package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.Plan
import com.miruni.feature.calendar.domain.model.PlanPriority
import com.miruni.feature.calendar.domain.model.PlanStatus
import com.miruni.feature.calendar.domain.model.PlanType

data class BasicPlanResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val startDateTime: String, // yyyy-MM-dd'T'HH:mm:ss
    val endDateTime: String, // yyyy-MM-dd'T'HH:mm:ss
    val expectedDuration: Int,
    val status: String,
    val priority: String
) {
    fun toDomain(): Plan {
        return Plan(
            userId = userId,
            planId = id,
            planType = PlanType.BASIC,
            title = title,
            subTitle = "",
            description = description,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            expectedDuration = expectedDuration,
            status = PlanStatus.fromServer(status),
            priority = PlanPriority.fromServer(priority),
            isDone = status == "DONE"
        )
    }
}