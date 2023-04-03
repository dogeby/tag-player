package com.dogeby.tagplayer.ui.tagdetail

import com.dogeby.tagplayer.domain.video.VideoItem

sealed interface TagDetailUiState {

    object Loading : TagDetailUiState

    object Empty : TagDetailUiState

    data class Success(
        val tagId: Long,
        val tagName: String,
        val videoItems: List<VideoItem>
    ) : TagDetailUiState
}
