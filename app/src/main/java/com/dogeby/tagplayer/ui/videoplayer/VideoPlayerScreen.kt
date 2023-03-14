package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun VideoPlayerRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel = hiltViewModel(),
) {
    val videoPlayerPagerUiState: VideoPlayerPagerUiState by viewModel.videoPlayerPagerUiState.collectAsState()

    VideoPlayerScreen(
        videoPlayerPagerUiState = videoPlayerPagerUiState,
        onPlayerSettledPageChanged = viewModel::onPlayerSettledPageChanged,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@Composable
fun VideoPlayerScreen(
    videoPlayerPagerUiState: VideoPlayerPagerUiState,
    onPlayerSettledPageChanged: (videoId: Long) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController) {
        systemUiController.isSystemBarsVisible = false
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            systemUiController.isSystemBarsVisible = true
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
    }

    when (videoPlayerPagerUiState) {
        VideoPlayerPagerUiState.Loading -> { /*TODO*/ }

        VideoPlayerPagerUiState.Empty -> { /*TODO*/ }

        is VideoPlayerPagerUiState.Success -> {
            VideoPlayerPager(
                currentPageVideoId = videoPlayerPagerUiState.currentVideoId,
                videoItems = videoPlayerPagerUiState.videoItems,
                onSettledPageChanged = onPlayerSettledPageChanged,
                onArrowBackButtonClick = onNavigateUp,
                modifier = modifier.fillMaxSize()
            )
        }
    }
}
