package com.dogeby.tagplayer.ui.videolist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.permission.RequiredPermissionsCheck

const val videoListNavigationRoute = "video_list_route"

fun NavController.navigateToVideoList(navOptions: NavOptions? = null) {
    navigate(
        route = videoListNavigationRoute,
        navOptions = navOptions,
    )
}

fun NavController.navigateToVideoList(builder: NavOptionsBuilder.() -> Unit) {
    navigateToVideoList(navOptions = navOptions(builder))
}

fun NavGraphBuilder.videoListScreen(
    tagPlayerDrawerItems: List<TagPlayerDrawerItem>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    onNavigateToVideoSearch: () -> Unit,
) {
    composable(videoListNavigationRoute) {
        RequiredPermissionsCheck()
        VideoListRoute(
            tagPlayerDrawerItems = tagPlayerDrawerItems,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToFilterSetting = onNavigateToFilterSetting,
            onNavigateToTagSetting = onNavigateToTagSetting,
            onNavigateToVideoSearch = onNavigateToVideoSearch,
        )
    }
}
