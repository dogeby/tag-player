package com.dogeby.tagplayer.ui.videofilter

sealed interface VideoFilterUiState {

    object Empty : VideoFilterUiState

    object Loading : VideoFilterUiState

    data class Success(
        val tagFilters: List<VideoTagFilterUiState>
    ) : VideoFilterUiState
}

data class VideoTagFilterUiState(
    val tagId: Long,
    val tagName: String,
    val isFilteredTag: Boolean,
)
