package com.dogeby.tagplayer.ui.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.preferences.app.GetAppPreferencesDataUseCase
import com.dogeby.tagplayer.domain.preferences.app.SetRejectedUpdateVersionCodeUseCase
import com.dogeby.tagplayer.domain.video.UpdateVideoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TagPlayerViewModel @Inject constructor(
    getAppPreferencesDataUseCase: GetAppPreferencesDataUseCase,
    private val updateVideoListUseCase: UpdateVideoListUseCase,
    private val setRejectedUpdateVersionCodeUseCase: SetRejectedUpdateVersionCodeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val appPreferencesData = getAppPreferencesDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val isAppUpdateCheck = savedStateHandle.getStateFlow(IS_APP_UPDATE_CHECK_KEY, false)

    fun updateVideoList() {
        viewModelScope.launch {
            updateVideoListUseCase()
        }
    }

    fun setRejectedUpdateVersionCode(versionCode: Int) {
        viewModelScope.launch {
            setRejectedUpdateVersionCodeUseCase(versionCode)
        }
    }

    fun setIsAppUpdateCheck(isAppUpdateCheck: Boolean) {
        savedStateHandle[IS_APP_UPDATE_CHECK_KEY] = isAppUpdateCheck
    }

    private companion object {

        const val IS_APP_UPDATE_CHECK_KEY = "isAppUpdateCheckKey"
    }
}
