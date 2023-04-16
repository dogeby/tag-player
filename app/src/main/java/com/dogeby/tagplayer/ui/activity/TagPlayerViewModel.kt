package com.dogeby.tagplayer.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.preferences.app.GetAppPreferencesDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TagPlayerViewModel @Inject constructor(
    getAppPreferencesDataUseCase: GetAppPreferencesDataUseCase,
) : ViewModel() {

    val appPreferencesData = getAppPreferencesDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
