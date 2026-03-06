package com.miruni.feature.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.miruni.core.navigation.MyPageRoute
import com.miruni.core.navigation.NavigationDestination
import com.miruni.feature.mypage.account.SettingAccountScreen
import com.miruni.feature.mypage.info.InformationScreen
import com.miruni.feature.mypage.info.feedback.ShowFeedbackHistory
import com.miruni.feature.mypage.info.feedback.SubmitFeedbackScreen
import com.miruni.feature.mypage.info.feedback.WriteFeedbackScreen
import com.miruni.feature.mypage.notification.SettingNotificationScreen
import jakarta.inject.Inject

class MyPageNavigation @Inject constructor(
//    override val arguments: List<NamedNavArgument>
) : NavigationDestination {
    override val route: String = MyPageRoute.MyPage.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        builder.composable(MyPageRoute.MyPage.route) {
            com.miruni.feature.mypage.MyPageRoute(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageSettingAccount.route) {
            SettingAccountScreen(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageSettingNotification.route) {
            SettingNotificationScreen(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageInfo.route) {
            InformationScreen(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageWriteFeedback.route) {
            WriteFeedbackScreen(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageSubmitFeedback.route) {
            SubmitFeedbackScreen(navController = navController)
        }

        builder.composable(MyPageRoute.MyPageShowFeedbackHistory.route) {
            ShowFeedbackHistory(navController = navController)
        }
    }
}
