package com.dogeby.tagplayer.datastore.videolist

import kotlinx.coroutines.flow.Flow

interface VideoListPreferencesDataSource {

    val videoListPreferencesData: Flow<VideoListPreferencesData>

    suspend fun setTagFilter(tagIds: List<Long>)

    suspend fun setSortType(sortType: VideoListSortType)

    suspend fun setDirectoryFilter(directoryNames: List<String>)
}
