package com.dogeby.tagplayer.ui.taglist

import com.dogeby.tagplayer.domain.tag.TagItem

sealed interface TagListUiState {

    object Loading : TagListUiState

    object Empty : TagListUiState

    data class Success(
        val tagItems: List<TagItem>,
    ) : TagListUiState
}
