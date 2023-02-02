package com.dogeby.tagplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dogeby.tagplayer.ui.permission.AppPermissionDeniedByExternalAction
import com.dogeby.tagplayer.ui.permission.PermissionScreen
import com.dogeby.tagplayer.ui.tagsetting.TagSettingRoute
import com.dogeby.tagplayer.ui.videolist.VideoListRoute
import com.google.gson.Gson

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
            AppPermissionDeniedByExternalAction(onExit)
            VideoListRoute(
                onNavigateToPlayer = { /*TODO*/ },
                onNavigateToFilterSetting = { /*TODO*/ },
                onNavigateToTagSetting = { videoIds ->
                    navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
                },
            )
        }
        composable(
            route = "$TagSettingRoute/{$VideoIdsArgument}",
            arguments = listOf(navArgument(VideoIdsArgument) { type = NavType.StringType }),
        ) {
            TagSettingRoute(
                modifier = modifier
            )
        }
    }
}
