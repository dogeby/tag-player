package com.dogeby.tagplayer.ui.tagsetting

import androidx.annotation.StringRes

sealed interface TagNameEditDialogUiState {

    data class Show(
        val tagId: Long,
        val originalName: String,
        val isError: Boolean,
        @StringRes val supportingTextResId: Int? = null,
    ) : TagNameEditDialogUiState

    object Hide : TagNameEditDialogUiState
}
