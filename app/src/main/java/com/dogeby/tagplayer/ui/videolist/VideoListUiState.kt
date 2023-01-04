package com.dogeby.tagplayer.ui.videolist

import com.dogeby.tagplayer.domain.video.VideoItem

sealed interface VideoListUiState {
    object Loading : VideoListUiState

    data class Success(
        val videoItems: List<VideoItem>,
    ) : VideoListUiState
}
