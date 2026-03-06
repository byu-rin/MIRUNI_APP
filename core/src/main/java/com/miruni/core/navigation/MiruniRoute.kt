package com.miruni.core.navigation

sealed class MiruniRoute(val route: String) {
    data object Splash : MiruniRoute("splash")
    data object AppOnboarding : MiruniRoute("app/onboarding") // 앱 최초 실행 온보딩

    data object Calendar : MiruniRoute("calendar")

    data object Login : MiruniRoute("login")
    data object SignUp : MiruniRoute("signup")
    data object PwReset : MiruniRoute("pwReset")
    data object AiPlannerMain : MiruniRoute("aiPlanner/main")
    data object AiPlannerOnboarding : MiruniRoute("aiPlanner/onboarding")
    data object AiPlannerPlanning : MiruniRoute("aiPlanner/planning")
    data object AiPlannerLoading : MiruniRoute("aiPlanner/loading")
    data object AiPlannerSchedule : MiruniRoute("aiPlanner/schedule")

    data object Survey : MiruniRoute("survey")
}
