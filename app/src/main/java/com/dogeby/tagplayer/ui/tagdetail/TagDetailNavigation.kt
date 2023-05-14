package com.dogeby.tagplayer.ui.tagdetail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions

private const val tagDetailNavigationRoute = "tag_detail_route"
private const val tagDetailTagIdArg = "tag_detail_tag_id"

class TagDetailArgs(val tagId: Long) {

    constructor(savedStateHandle: SavedStateHandle) :
        this(tagId = checkNotNull(savedStateHandle[tagDetailTagIdArg]))
}

fun NavController.navigateToTagDetail(tagId: Long, navOptions: NavOptions? = null) {
    navigate(
        route = "$tagDetailNavigationRoute/$tagId",
        navOptions = navOptions,
    )
}

fun NavController.navigateToTagDetail(tagId: Long, builder: NavOptionsBuilder.() -> Unit) {
    navigateToTagDetail(
        tagId = tagId,
        navOptions = navOptions(builder),
    )
}

fun NavGraphBuilder.tagDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
) {
    composable(
        route = "$tagDetailNavigationRoute/{$tagDetailTagIdArg}",
        arguments = listOf(navArgument(tagDetailTagIdArg) { type = NavType.LongType }),
    ) {
        TagDetailRoute(
            onNavigateUp = onNavigateUp,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToTagSetting = onNavigateToTagSetting,
        )
    }
}
