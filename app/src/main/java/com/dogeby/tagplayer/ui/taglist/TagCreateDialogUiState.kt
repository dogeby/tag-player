package com.dogeby.tagplayer.ui.taglist

import androidx.annotation.StringRes

sealed interface TagCreateDialogUiState {

    data class Show(
        val isError: Boolean = false,
        @StringRes val supportingTextResId: Int? = null,
    ) : TagCreateDialogUiState

    object Hide : TagCreateDialogUiState
}
