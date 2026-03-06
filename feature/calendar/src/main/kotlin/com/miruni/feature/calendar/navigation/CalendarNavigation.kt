package com.miruni.feature.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.miruni.core.navigation.MiruniRoute
import com.miruni.core.navigation.NavigationDestination
import com.miruni.feature.calendar.presentation.CalendarRoute
import jakarta.inject.Inject

class CalendarNavigation @Inject constructor(
//    override val arguments: List<NamedNavArgument>
) : NavigationDestination {
    override val route: String = MiruniRoute.Calendar.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        builder.composable(route) {
            CalendarRoute(
                navController = navController
            )
        }
    }
}