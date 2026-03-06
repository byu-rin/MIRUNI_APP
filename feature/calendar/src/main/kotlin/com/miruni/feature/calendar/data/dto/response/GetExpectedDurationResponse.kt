package com.miruni.feature.calendar.data.dto.response

data class GetExpectedDurationResponse(
    val planType: String,
    val id: Int,
    val expectedDuration: Int
)
