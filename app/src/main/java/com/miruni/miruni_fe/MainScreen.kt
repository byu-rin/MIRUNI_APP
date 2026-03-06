package com.miruni.miruni_fe

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miruni.core.navigation.HomeRoute
import com.miruni.core.navigation.MiruniRoute
import com.miruni.core.navigation.MyPageRoute
import com.miruni.core.navigation.NavigationDestination

@Composable
fun MainScreen(destinations: Set<NavigationDestination>) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination

    // bottom bar를 표시할 대상 route 리스트
    val bottomBarRoutes = listOf(
        HomeRoute.Home.route,
        MiruniRoute.AiPlannerMain.route,
        MiruniRoute.Calendar.route,
        MyPageRoute.MyPage.route
    )

    val bottomBarVisible = currentDestination?.hierarchy?.any { it.route in bottomBarRoutes } == true

    Scaffold(
        bottomBar = {
            if (bottomBarVisible) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MiruniRoute.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            destinations.forEach { destination ->
                destination.register(this, navController) }
        }
    }
}
