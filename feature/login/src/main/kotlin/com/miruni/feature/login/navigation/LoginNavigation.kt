package com.miruni.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.miruni.core.navigation.HomeRoute
import com.miruni.core.navigation.MiruniRoute
import com.miruni.core.navigation.NavigationDestination
import com.miruni.feature.login.LoginScreen
import jakarta.inject.Inject

class LoginNavigation @Inject constructor(
//    override val arguments: List<NamedNavArgument>
) : NavigationDestination {
    override val route: String = MiruniRoute.Login.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        builder.composable(route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute.Home.route)
                },
                onSignUpClick = {
                    navController.navigate(MiruniRoute.SignUp.route)
                },
                onResetPasswordClick = {
                    navController.navigate(MiruniRoute.PwReset.route)
                }
            )
        }
    }
}