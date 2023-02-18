package com.dogeby.tagplayer.data.preferences

import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesData
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val videoListPreferencesDataSource: VideoListPreferencesDataSource,
) : PreferencesRepository {

    override val videoListPreferencesData: Flow<VideoListPreferencesData>
        get() = videoListPreferencesDataSource.videoListPreferencesData

    override suspend fun setTagFilter(tagIds: List<Long>) {
        videoListPreferencesDataSource.setTagFilter(tagIds)
    }
}
