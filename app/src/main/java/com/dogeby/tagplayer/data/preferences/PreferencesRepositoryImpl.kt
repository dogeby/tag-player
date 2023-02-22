package com.dogeby.tagplayer.data.preferences

import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesData
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesDataSource
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
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

    override suspend fun setSortType(sortType: VideoListSortType) {
        videoListPreferencesDataSource.setSortType(sortType)
    }

    override suspend fun setDirectoryFilter(directoryNames: List<String>) {
        videoListPreferencesDataSource.setDirectoryFilter(directoryNames)
    }
}
