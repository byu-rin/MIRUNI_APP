package com.miruni.core.navigation

sealed class HomeRoute(val route: String) {
    data object Home : HomeRoute("home")
    data object HomeDndOnboarding : HomeRoute("home/dnd/onboarding")
    data object HomeDndTimerSetting : HomeRoute("home/dnd/timerSetting")

    data object HomeDndPause : HomeRoute("home/dnd/pause/{hour}/{minute}") {
        fun createRoute(hour: Int, minute: Int): String {
            return "home/dnd/pause/$hour/$minute"
        }
    }

    data object HomeDndEarlyEnd : HomeRoute("home/dnd/earlyEnd/{hour}/{minute}") {
        fun createRoute(hour: Int, minute: Int) : String {
            return "home/dnd/earlyEnd/$hour/$minute"
        }
    }

    data object HomeDndComplete : HomeRoute("home/dnd/complete/{hour}/{minute}") {
        fun createRoute(hour: Int, minute: Int) : String {
            return "home/dnd/complete/$hour/$minute"
        }
    }

    data object RunScheduleTimerSetting : HomeRoute("runScheduleTimerSetting")
    data object AlarmLogs : HomeRoute("alarmLogs")
    data object Dnd : HomeRoute("dnd")
    data object Execution : HomeRoute("execution")
}

sealed class HomeModalRoute(val route: String) {
    data object Error : HomeModalRoute("home/rerun/TimerErrorModal")
    data object Setting : HomeModalRoute("home/rerun/TimerSettingModal/{hour}/{minute}")
}
