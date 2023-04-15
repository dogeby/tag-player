package com.dogeby.tagplayer.ui.apppreferences

import com.dogeby.tagplayer.datastore.app.AppThemeMode

sealed interface AppPreferencesUiState {

    object Loading : AppPreferencesUiState

    data class Success(
        val appThemeMode: AppThemeMode,
    ) : AppPreferencesUiState
}
