package com.dogeby.tagplayer.data.preferences

import com.dogeby.tagplayer.datastore.app.AppPreferencesData
import com.dogeby.tagplayer.datastore.app.AppPreferencesDataSource
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesData
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesDataSource
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val videoListPreferencesDataSource: VideoListPreferencesDataSource,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : PreferencesRepository {

    override val videoListPreferencesData: Flow<VideoListPreferencesData>
        get() = videoListPreferencesDataSource.videoListPreferencesData

    override val appPreferencesData: Flow<AppPreferencesData>
        get() = appPreferencesDataSource.appPreferencesData

    override suspend fun setTagFilter(tagIds: List<Long>) {
        videoListPreferencesDataSource.setTagFilter(tagIds)
    }

    override suspend fun setSortType(sortType: VideoListSortType) {
        videoListPreferencesDataSource.setSortType(sortType)
    }

    override suspend fun setDirectoryFilter(directoryNames: List<String>) {
        videoListPreferencesDataSource.setDirectoryFilter(directoryNames)
    }

    override suspend fun setAppThemeMode(appThemeMode: AppThemeMode) {
        appPreferencesDataSource.setAppThemeMode(appThemeMode)
    }

    override suspend fun setAutoRotation(isAutoRotation: Boolean) {
        appPreferencesDataSource.setAutoRotation(isAutoRotation)
    }
}
