package com.dogeby.tagplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dogeby.tagplayer.ui.permission.PermissionScreen
import com.dogeby.tagplayer.ui.videolist.VideoListRoute

@Composable
fun TagPlayerNavHost(
    onExit: () -> Unit,
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
                    navController.navigate(VideoListRoute) {
                        popUpTo(PermissionRoute) { inclusive = true }
                    }
                },
            )
        }
        composable(VideoListRoute) {
            VideoListRoute(
                onExit = onExit,
                onNavigateToPlayer = { /*TODO*/ },
                onNavigateToFilterSetting = { /*TODO*/ },
            )
        }
    }
}
