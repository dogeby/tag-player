package com.dogeby.tagplayer.ui.activity

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
) : ViewModel() {

    val appPreferencesData = getAppPreferencesDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

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
}
