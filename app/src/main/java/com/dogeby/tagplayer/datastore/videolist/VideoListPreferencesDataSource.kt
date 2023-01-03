package com.dogeby.tagplayer.datastore.videolist

import kotlinx.coroutines.flow.Flow

interface VideoListPreferencesDataSource {

    val videoListPreferencesData: Flow<VideoListPreferencesData>

    suspend fun setFilteredTagIds(tagIds: Set<Long>)
}
