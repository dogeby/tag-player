package com.dogeby.tagplayer.ui.apppreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.domain.preferences.app.GetAppPreferencesDataUseCase
import com.dogeby.tagplayer.domain.preferences.app.SetAppThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    getAppPreferencesDataUseCase: GetAppPreferencesDataUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
) : ViewModel() {

    val appPreferencesUiState = getAppPreferencesDataUseCase()
        .map {
            AppPreferencesUiState.Success(
                appThemeMode = it.appThemeMode,
            )
        }
        .onStart {
            AppPreferencesUiState.Loading
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppPreferencesUiState.Loading,
        )

    fun setAppThemeMode(appThemeMode: AppThemeMode) {
        viewModelScope.launch {
            setAppThemeModeUseCase(appThemeMode)
        }
    }
}
