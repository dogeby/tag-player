package com.dogeby.tagplayer.ui.videolist

sealed interface FilteredTagsUiState {
    object Loading : FilteredTagsUiState

    data class Success(
        val filteredTagNames: List<String>,
    ) : FilteredTagsUiState
}
