package com.dogeby.tagplayer.ui.videofilter

sealed interface VideoFilterUiState {

    object Empty : VideoFilterUiState

    object Loading : VideoFilterUiState

    data class Success(
        val directoryFilterUiState: VideoDirectoryFilterUiState,
        val tagFilterUiState: VideoTagFilterUiState,
    ) : VideoFilterUiState
}

sealed interface VideoDirectoryFilterUiState {

    object Empty : VideoDirectoryFilterUiState

    data class Success(
        val directoryFilterItems: List<VideoDirectoryFilterItemUiState>,
    ) : VideoDirectoryFilterUiState
}

sealed interface VideoTagFilterUiState {

    object Empty : VideoTagFilterUiState

    data class Success(
        val tagFilterItems: List<VideoTagFilterItemUiState>,
    ) : VideoTagFilterUiState
}

data class VideoDirectoryFilterItemUiState(
    val name: String,
    val isFiltered: Boolean,
)

data class VideoTagFilterItemUiState(
    val tagId: Long,
    val tagName: String,
    val isFilteredTag: Boolean,
)
