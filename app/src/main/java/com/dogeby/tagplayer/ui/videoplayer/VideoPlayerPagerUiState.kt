package com.dogeby.tagplayer.ui.videoplayer

import com.dogeby.tagplayer.domain.video.VideoItem

sealed interface VideoPlayerPagerUiState {

    object Loading : VideoPlayerPagerUiState

    object Empty : VideoPlayerPagerUiState

    data class Success(
        val currentVideoId: Long,
        val videoItems: List<VideoItem>,
    ) : VideoPlayerPagerUiState
}
