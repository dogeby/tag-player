package com.dogeby.tagplayer.ui.taglist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem

const val tagListNavigationRoute = "tag_list_route"

fun NavController.navigateToTagList(navOptions: NavOptions? = null) {
    navigate(
        route = tagListNavigationRoute,
        navOptions = navOptions,
    )
}

fun NavController.navigateToTagList(builder: NavOptionsBuilder.() -> Unit) {
    navigateToTagList(navOptions = navOptions(builder))
}

fun NavGraphBuilder.tagListScreen(
    tagPlayerDrawerItems: List<TagPlayerDrawerItem>,
    onNavigateToTagDetail: (Long) -> Unit,
) {
    composable(tagListNavigationRoute) {
        TagListRoute(
            tagPlayerDrawerItems = tagPlayerDrawerItems,
            onNavigateToTagDetail = onNavigateToTagDetail,
        )
    }
}
