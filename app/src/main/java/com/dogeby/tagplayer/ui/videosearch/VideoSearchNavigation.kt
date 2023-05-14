package com.dogeby.tagplayer.ui.videosearch

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.dogeby.tagplayer.ui.permission.AppPermissionCheck

private const val videoSearchNavigationRoute = "video_search_route"

fun NavController.navigateToVideoSearch(navOptions: NavOptions? = null) {
    navigate(
        route = videoSearchNavigationRoute,
        navOptions = navOptions
    )
}

fun NavController.navigateToVideoSearch(builder: NavOptionsBuilder.() -> Unit) {
    navigateToVideoSearch(navOptions = navOptions(builder))
}

fun NavGraphBuilder.videoSearchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
) {
    composable(videoSearchNavigationRoute) {
        AppPermissionCheck()
        VideoSearchRoute(
            onNavigateUp = onNavigateUp,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToTagSetting = onNavigateToTagSetting,
        )
    }
}
