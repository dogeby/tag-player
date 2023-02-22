package com.dogeby.tagplayer.datastore.videolist

data class VideoListPreferencesData(
    val filteredTagIds: List<Long>,
    val filteredDirectoryNames: List<String>,
    val sortType: VideoListSortType,
)
