package com.dogeby.tagplayer.ui.videolist

import com.dogeby.tagplayer.domain.video.VideoItem

sealed interface VideoInfoDialogUiState {

    object Hide : VideoInfoDialogUiState

    data class ShowSingleInfo(
        val videoItem: VideoItem,
    ) : VideoInfoDialogUiState

    data class ShowMultiInfo(
        val representativeName: String,
        val count: Int,
        val totalSize: String,
    ) : VideoInfoDialogUiState
}
