package com.dogeby.tagplayer.ui.apppreferences

sealed interface LoadTagsResultUiState {

    object Nothing : LoadTagsResultUiState

    object Loading : LoadTagsResultUiState

    object Failure : LoadTagsResultUiState

    object Success : LoadTagsResultUiState
}
