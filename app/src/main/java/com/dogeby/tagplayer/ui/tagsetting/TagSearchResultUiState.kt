package com.dogeby.tagplayer.ui.tagsetting

sealed interface TagSearchResultUiState {

    object Loading : TagSearchResultUiState

    object Empty : TagSearchResultUiState

    data class Success(
        val tags: List<TagSearchResultItemUiState>,
        val keyword: String = "",
        val isShowTagCreateText: Boolean = false,
    ) : TagSearchResultUiState
}

data class TagSearchResultItemUiState(
    val id: Long = 0,
    val name: String,
    val isIncluded: Boolean,
)
