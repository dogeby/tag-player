package com.dogeby.tagplayer.ui.videolist

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.domain.preferences.GetIsTagFilteredUseCase
import com.dogeby.tagplayer.domain.preferences.GetVideoListSortTypeUseCase
import com.dogeby.tagplayer.domain.preferences.SetVideoListSortTypeUseCase
import com.dogeby.tagplayer.domain.video.FormatSizeUseCase
import com.dogeby.tagplayer.domain.video.GetVideoItemsUseCase
import com.dogeby.tagplayer.domain.video.UpdateVideoListUseCase
import com.dogeby.tagplayer.domain.video.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class VideoListViewModel @Inject constructor(
    getVideoItemsUseCase: GetVideoItemsUseCase,
    getIsTagFilteredUseCase: GetIsTagFilteredUseCase,
    getVideoListSortTypeUseCase: GetVideoListSortTypeUseCase,
    private val updateVideoListUseCase: UpdateVideoListUseCase,
    private val formatSizeUseCase: FormatSizeUseCase,
    private val setVideoListSortTypeUseCase: SetVideoListSortTypeUseCase,
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

    private val _videoInfoDialogUiState: MutableStateFlow<VideoInfoDialogUiState> = MutableStateFlow(VideoInfoDialogUiState.Hide)
    val videoInfoDialogUiState: StateFlow<VideoInfoDialogUiState> = _videoInfoDialogUiState.asStateFlow()

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

    fun showVideoInfoDialog() {
        val videoListUiState = videoListUiState.value
        if (videoListUiState !is VideoListUiState.Success) return

        val selectedVideoIds = isSelectedVideoItems.filterValues { it }.keys
        _videoInfoDialogUiState.value = when (selectedVideoIds.count()) {
            0 -> {
                VideoInfoDialogUiState.Hide
            }
            1 -> {
                videoListUiState.videoItems.find { it.id == selectedVideoIds.first() }?.let {
                    VideoInfoDialogUiState.ShowSingleInfo(it)
                } ?: VideoInfoDialogUiState.Hide
            }
            else -> {
                val videoItems = videoListUiState.videoItems.filter { selectedVideoIds.contains(it.id) }
                VideoInfoDialogUiState.ShowMultiInfo(
                    representativeName = videoItems.first().name,
                    count = videoItems.count(),
                    totalSize = formatSizeUseCase(
                        videoItems.fold(0L) { acc: Long, videoItem: VideoItem -> acc + videoItem.size }
                    ),
                )
            }
        }
    }

    fun hideVideoInfoDialog() {
        _videoInfoDialogUiState.value = VideoInfoDialogUiState.Hide
    }

    fun setSortType(videoListSortType: VideoListSortType) {
        viewModelScope.launch {
            setVideoListSortTypeUseCase(videoListSortType)
        }
    }
}
