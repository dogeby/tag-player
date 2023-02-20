package com.dogeby.tagplayer.ui.videolist
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType

data class VideoListSortTypeUiState(
    val sortTypes: List<VideoListSortTypeItemUiState>,
)

data class VideoListSortTypeItemUiState(
    val sortType: VideoListSortType,
    val isSelected: Boolean,
)
