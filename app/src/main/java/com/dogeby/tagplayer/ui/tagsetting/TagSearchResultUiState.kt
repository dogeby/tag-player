package com.dogeby.tagplayer.ui.tagsetting

sealed interface TagSearchResultUiState {

    object Loading : TagSearchResultUiState

    data class EmptySearchResult(
        val keyword: String,
    ) : TagSearchResultUiState

    data class Success(
        val tags: List<TagSearchResultItemUiState>
    ) : TagSearchResultUiState
}

data class TagSearchResultItemUiState(
    val id: Long = 0,
    val name: String,
    val isIncluded: Boolean,
)
