package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VideoPlayerRoute(
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel = hiltViewModel(),
) {
    val videoPlayerPagerUiState: VideoPlayerPagerUiState by viewModel.videoPlayerPagerUiState.collectAsState()

    VideoPlayerScreen(
        videoPlayerPagerUiState = videoPlayerPagerUiState,
        onPlayerSettledPageChanged = viewModel::onPlayerSettledPageChanged,
        modifier = modifier,
    )
}

@Composable
fun VideoPlayerScreen(
    videoPlayerPagerUiState: VideoPlayerPagerUiState,
    onPlayerSettledPageChanged: (videoId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (videoPlayerPagerUiState) {
        VideoPlayerPagerUiState.Loading -> { /*TODO*/ }

        VideoPlayerPagerUiState.Empty -> { /*TODO*/ }

        is VideoPlayerPagerUiState.Success -> {
            VideoPlayerPager(
                currentPageVideoId = videoPlayerPagerUiState.currentVideoId,
                videoItems = videoPlayerPagerUiState.videoItems,
                onSettledPageChanged = onPlayerSettledPageChanged,
                modifier = modifier.fillMaxSize()
            )
        }
    }
}
