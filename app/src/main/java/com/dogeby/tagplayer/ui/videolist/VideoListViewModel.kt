package com.dogeby.tagplayer.ui.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.preferences.GetFilteredTagUseCase
import com.dogeby.tagplayer.domain.video.GetVideoItemsUseCase
import com.dogeby.tagplayer.domain.video.UpdateVideoListUseCase
import com.dogeby.tagplayer.domain.video.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoListViewModel @Inject constructor(
    getFilteredTagUseCase: GetFilteredTagUseCase,
    getVideoItemsUseCase: GetVideoItemsUseCase,
    private val updateVideoListUseCase: UpdateVideoListUseCase,
) : ViewModel() {

    val filteredTagsUiState: StateFlow<FilteredTagsUiState> = getFilteredTagUseCase()
        .map<List<Tag>, FilteredTagsUiState> { tags -> FilteredTagsUiState.Success(tags.map { it.name }) }
        .onStart { emit(FilteredTagsUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FilteredTagsUiState.Loading,
        )

    val videoListUiState: StateFlow<VideoListUiState> = getVideoItemsUseCase()
        .map<List<VideoItem>, VideoListUiState>(VideoListUiState::Success)
        .onStart { emit(VideoListUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoListUiState.Loading,
        )

    suspend fun updateVideoList() = updateVideoListUseCase()
}
