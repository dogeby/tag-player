package com.dogeby.tagplayer.data.preferences

import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesData
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    val videoListPreferencesData: Flow<VideoListPreferencesData>

    suspend fun setTagFilter(tagIds: List<Long>)
}
