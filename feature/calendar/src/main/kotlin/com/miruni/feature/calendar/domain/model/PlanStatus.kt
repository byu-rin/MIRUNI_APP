package com.miruni.feature.calendar.domain.model

enum class PlanStatus(
    val server: String,
    val ui: String
) {
    TODO("TODO", "예정"),
    IN_PROGRESS("IN_PROGRESS", "진행 중"),
    DONE("DONE", "완료");

    companion object {
        /** server -> PlanStatus */
        fun fromServer(server: String?): PlanStatus = entries.find {
            it.server == server
        } ?: TODO

        /** ui -> PlanStatus */
        fun fromUi(ui: String): PlanStatus = entries.find {
            it.ui == ui
        } ?: TODO
    }
}