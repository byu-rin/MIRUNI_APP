package com.miruni.feature.calendar.data.dto.response

import com.miruni.feature.calendar.domain.model.DayInfo

data class GetMonthlyPlansResponse(
    val date: String, // yyyy-MM-dd
    val unfinishedPlanCount: Int
) {
    fun toDomain(): DayInfo {
        return DayInfo(
            date = date,
            unfinishedPlanCount = unfinishedPlanCount
        )
    }
}