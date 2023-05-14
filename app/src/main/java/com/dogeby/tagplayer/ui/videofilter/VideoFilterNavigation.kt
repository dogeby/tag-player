package com.dogeby.tagplayer.ui.videofilter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions

private const val videoFilterNavigationRoute = "video_filter_route"

fun NavController.navigateToVideoFilter(navOptions: NavOptions? = null) {
    navigate(
        route = videoFilterNavigationRoute,
        navOptions = navOptions,
    )
}

fun NavController.navigateToVideoFilter(builder: NavOptionsBuilder.() -> Unit) {
    navigateToVideoFilter(navOptions = navOptions(builder))
}

fun NavGraphBuilder.videoFilterScreen(onNavigateUp: () -> Unit) {
    composable(videoFilterNavigationRoute) {
        VideoFilterRoute(onNavigateUp = onNavigateUp)
    }
}
