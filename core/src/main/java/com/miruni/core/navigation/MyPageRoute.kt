package com.miruni.core.navigation

sealed class MyPageRoute(val route: String) {
    data object MyPage : MyPageRoute("myPage")
    data object MyPageSettingAccount : MyPageRoute("myPageSettingAccount")
    data object MyPageSettingNotification : MyPageRoute("myPageSettingNotification")
    data object MyPageInfo : MyPageRoute("myPageInfo")
    data object MyPageWriteFeedback : MyPageRoute("myPageWriteFeedback")
    data object MyPageSubmitFeedback : MyPageRoute("myPageSubmitFeedback")
    data object MyPageShowFeedbackHistory : MyPageRoute("myPageShowFeedbackHistory")
}
