package com.miruni.feature.calendar.domain.model

enum class PlanType(
    val server: String,
    val ui: String
) {
    BASIC("BASIC", "일반"),
    AI("AI", "AI");

    companion object {
        /** server -> PlanStatus */
        fun fromServer(server: String?): PlanType = entries.find {
            it.server == server
        } ?: BASIC

        /** ui -> PlanStatus */
        fun fromUi(ui: String): PlanType = entries.find {
            it.ui == ui
        } ?: BASIC
    }
}