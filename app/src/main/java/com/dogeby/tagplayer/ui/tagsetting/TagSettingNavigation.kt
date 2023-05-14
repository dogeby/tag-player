package com.dogeby.tagplayer.ui.tagsetting

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.google.gson.Gson

private const val tagSettingNavigationRoute = "tag_setting_route"
private const val tagSettingVideoIdsArg = "tag_setting_video_Ids"

class TagSettingArgs(val videoIds: List<Long>) {

    constructor(savedStateHandle: SavedStateHandle) :
        this(
            Gson().fromJson(
                checkNotNull<String>(savedStateHandle[tagSettingVideoIdsArg]),
                LongArray::class.java,
            ).toList()
        )
}

fun NavController.navigateToTagSetting(videoIds: List<Long>, navOptions: NavOptions? = null) {
    navigate(
        route = "$tagSettingNavigationRoute/${Gson().toJson(videoIds)}",
        navOptions = navOptions
    )
}

fun NavController.navigateToTagSetting(videoIds: List<Long>, builder: NavOptionsBuilder.() -> Unit) {
    navigateToTagSetting(
        videoIds = videoIds,
        navOptions = navOptions(builder)
    )
}

fun NavGraphBuilder.tagSettingScreen(
    onNavigateUp: () -> Unit = {},
) {
    composable(
        route = "$tagSettingNavigationRoute/{$tagSettingVideoIdsArg}",
        arguments = listOf(navArgument(tagSettingVideoIdsArg) { type = NavType.StringType }),
    ) {
        TagSettingRoute(onNavigateUp = onNavigateUp)
    }
}
