package com.dogeby.tagplayer.ui.videosearch

import com.dogeby.tagplayer.ui.videolist.VideoListUiState

sealed interface VideoSearchViewUiState {

    object Empty : VideoSearchViewUiState

    object QueryBlank : VideoSearchViewUiState

    data class Success(val videoListUiState: VideoListUiState.Success) : VideoSearchViewUiState
}
