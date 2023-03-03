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
import com.dogeby.tagplayer.ui.videofilter.VideoFilterRoute
import com.dogeby.tagplayer.ui.videolist.VideoListRoute
import com.dogeby.tagplayer.ui.videoplayer.VideoPlayerRoute
import com.dogeby.tagplayer.ui.videosearch.VideoSearchRoute
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
                onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
                onNavigateToFilterSetting = { navController.navigate(VideoFilterRoute) },
                onNavigateToTagSetting = { videoIds ->
                    navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
                },
                onNavigateToVideoSearch = { navController.navigate(VideoSearchRoute) }
            )
        }
        composable(
            route = "$TagSettingRoute/{$TagSettingVideoIdsArgument}",
            arguments = listOf(navArgument(TagSettingVideoIdsArgument) { type = NavType.StringType }),
        ) {
            TagSettingRoute(
                modifier = modifier,
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(VideoSearchRoute) {
            AppPermissionDeniedByExternalAction(onExit)
            VideoSearchRoute(
                onNavigateToPlayer = { _, _ -> /*TODO*/ },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(VideoFilterRoute) {
            VideoFilterRoute(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = "$VideoPlayerRoute/{$VideoPlayerVideoIdsArgument}/{$VideoPlayerStartVideoId}",
            arguments = listOf(
                navArgument(VideoPlayerVideoIdsArgument) { type = NavType.StringType },
                navArgument(VideoPlayerStartVideoId) { type = NavType.LongType },
            )
        ) {
            VideoPlayerRoute()
        }
    }
}
