package com.dogeby.tagplayer.ui.videolist

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.preferences.GetIsTagFilteredUseCase
import com.dogeby.tagplayer.domain.video.GetVideoItemsUseCase
import com.dogeby.tagplayer.domain.video.UpdateVideoListUseCase
import com.dogeby.tagplayer.domain.video.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoListViewModel @Inject constructor(
    getVideoItemsUseCase: GetVideoItemsUseCase,
    getIsTagFilteredUseCase: GetIsTagFilteredUseCase,
    private val updateVideoListUseCase: UpdateVideoListUseCase,
) : ViewModel() {

    private val _isSelectMode = MutableStateFlow(false)
    val isSelectMode: StateFlow<Boolean> = _isSelectMode

    private val _isSelectedVideoItems = mutableStateMapOf<Long, Boolean>()
    val isSelectedVideoItems: Map<Long, Boolean> = _isSelectedVideoItems

    val videoListUiState: StateFlow<VideoListUiState> = getVideoItemsUseCase()
        .map<List<VideoItem>, VideoListUiState>(VideoListUiState::Success)
        .onStart { emit(VideoListUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoListUiState.Loading,
        )

    val isTagFiltered = getIsTagFilteredUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    suspend fun updateVideoList() = updateVideoListUseCase()

    fun toggleIsSelectedVideoItems(id: Long) {
        _isSelectedVideoItems.compute(id) { _, v ->
            v?.not() ?: true
        }

        if (isSelectedVideoItems.all { it.value.not() }) {
            clearIsSelectedVideoItems()
        } else {
            setSelectMode(true)
        }
    }

    fun selectAllVideoItems() {
        val videoListUiState = videoListUiState.value
        if (videoListUiState is VideoListUiState.Success) {
            _isSelectedVideoItems.putAll(
                videoListUiState.videoItems.associateBy(
                    keySelector = { it.id },
                    valueTransform = { true },
                ),
            )
        }
    }

    private fun setSelectMode(isSelectMode: Boolean) {
        _isSelectMode.value = isSelectMode
    }

    fun clearIsSelectedVideoItems() {
        setSelectMode(false)
        _isSelectedVideoItems.clear()
    }
}
