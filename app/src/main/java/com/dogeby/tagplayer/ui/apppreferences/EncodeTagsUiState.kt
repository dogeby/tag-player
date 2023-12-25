package com.dogeby.tagplayer.ui.apppreferences

sealed interface EncodeTagsUiState {

    object Loading : EncodeTagsUiState

    data class Success(
        val backupTagsJson: String,
    ) : EncodeTagsUiState
}
