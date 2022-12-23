package com.dogeby.tagplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dogeby.tagplayer.ui.permission.PermissionScreen

@Composable
fun TagPlayerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = PermissionRoute,
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        composable(PermissionRoute) {
            PermissionScreen(
                modifier = modifier,
                onNavigateToDestination = {
                    navController.navigate(HomeRoute) {
                        popUpTo(PermissionRoute) { inclusive = true }
                    }
                },
            )
        }
        composable(HomeRoute) {
            // TODO: HomeScreen 구현
        }
    }
}
