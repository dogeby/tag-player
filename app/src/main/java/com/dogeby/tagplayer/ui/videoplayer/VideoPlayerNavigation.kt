package com.dogeby.tagplayer.ui.videoplayer

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

private const val videoPlayerNavigationRoute = "video_player_route"
private const val videoPlayerStartVideoIdArg = "video_player_start_video_id"
private const val videoPlayerVideoIdsArg = "video_player_video_ids"

class VideoPlayerArgs(
    val startVideoId: Long,
    val videoIds: List<Long>,
) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(
            startVideoId = checkNotNull(savedStateHandle[videoPlayerStartVideoIdArg]),
            videoIds = Gson().fromJson(
                checkNotNull<String>(savedStateHandle[videoPlayerVideoIdsArg]),
                LongArray::class.java,
            ).toList()
        )
}

fun NavController.navigateToVideoPlayer(
    startVideoId: Long,
    videoIds: List<Long>,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = "$videoPlayerNavigationRoute/$startVideoId/${Gson().toJson(videoIds)}",
        navOptions = navOptions,
    )
}

fun NavController.navigateToVideoPlayer(
    startVideoId: Long,
    videoIds: List<Long>,
    builder: NavOptionsBuilder.() -> Unit,
) {
    navigateToVideoPlayer(
        startVideoId = startVideoId,
        videoIds = videoIds,
        navOptions = navOptions(builder),
    )
}

fun NavGraphBuilder.videoPlayerScreen(onNavigateUp: () -> Unit) {
    composable(
        route = "$videoPlayerNavigationRoute/{$videoPlayerStartVideoIdArg}/{$videoPlayerVideoIdsArg}",
        arguments = listOf(
            navArgument(videoPlayerStartVideoIdArg) { type = NavType.LongType },
            navArgument(videoPlayerVideoIdsArg) { type = NavType.StringType },
        )
    ) {
        VideoPlayerRoute(onNavigateUp = onNavigateUp)
    }
}
