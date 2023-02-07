package com.dogeby.tagplayer.ui.tagsetting

import com.dogeby.tagplayer.data.tag.Tag

data class TagInputChipTextFieldUiState(
    val commonTags: List<Tag> = emptyList(),
    val keyword: String = "",
)
