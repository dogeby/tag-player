package com.dogeby.tagplayer.ui.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.domain.preferences.videolist.GetIsVideoFilteredUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.GetVideoListSortTypeUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.SetVideoListSortTypeUseCase
import com.dogeby.tagplayer.domain.video.GetVideoItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class VideoListViewModel @Inject constructor(
    getVideoItemsUseCase: GetVideoItemsUseCase,
    getIsVideoFilteredUseCase: GetIsVideoFilteredUseCase,
    getVideoListSortTypeUseCase: GetVideoListSortTypeUseCase,
    private val setVideoListSortTypeUseCase: SetVideoListSortTypeUseCase,
) : ViewModel() {

    val videoListUiState: StateFlow<VideoListUiState> = getVideoItemsUseCase()
        .map { videoItems ->
            if (videoItems.isEmpty()) return@map VideoListUiState.Empty
            VideoListUiState.Success(videoItems)
        }
        .onStart { emit(VideoListUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoListUiState.Loading,
        )

    val isVideoFiltered = getIsVideoFilteredUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val videoListSortTypeUiState: StateFlow<VideoListSortTypeUiState> = getVideoListSortTypeUseCase()
        .map { selectedSortType ->
            val sortTypes = VideoListSortType.values().map {
                VideoListSortTypeItemUiState(it, it == selectedSortType)
            }
            VideoListSortTypeUiState(sortTypes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoListSortTypeUiState(emptyList())
        )

    fun setSortType(videoListSortType: VideoListSortType) {
        viewModelScope.launch {
            setVideoListSortTypeUseCase(videoListSortType)
        }
    }
}
