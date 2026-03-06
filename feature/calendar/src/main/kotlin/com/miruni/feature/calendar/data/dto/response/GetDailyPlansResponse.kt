package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.DailyPlans

data class GetDailyPlansResponse(
    val unfinishedPlan: List<DailyPlanItem>,
    val finishedPlan: List<DailyPlanItem>
) {
    fun toDomain(): DailyPlans {
        return DailyPlans(
            unfinishedPlan = unfinishedPlan.map { it.toDomain() },
            finishedPlan = finishedPlan.map { it.toDomain() }
        )
    }
}
