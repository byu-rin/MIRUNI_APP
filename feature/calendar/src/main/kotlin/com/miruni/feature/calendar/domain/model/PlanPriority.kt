package com.miruni.feature.calendar.domain.model

enum class PlanPriority(
    val server: String, // 서버에서 받는 값
    val ui: String // UI에서 사용하는 값
) {
    HIGH("HIGH", "상"),
    MEDIUM("MEDIUM", "중"),
    LOW("LOW", "하");

    companion object {
        /** server -> PlanPriority */
        fun fromServer(value: String?): PlanPriority = entries.find {
            it.server == value
        } ?: HIGH

        /** ui -> PlanPriority */
        fun fromUi(label: String): PlanPriority = entries.find {
            it.ui == label
        } ?: HIGH
    }
}