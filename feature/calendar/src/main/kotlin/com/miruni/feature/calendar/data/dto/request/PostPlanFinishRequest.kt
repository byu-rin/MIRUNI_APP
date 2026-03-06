package com.miruni.feature.calendar.data.dto.request

data class PostPlanFinishRequest(
    val expectedTime: String // 실행 예정으로 설정했던 시간 HH:mm 형식
)