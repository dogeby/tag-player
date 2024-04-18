package com.dogeby.tagplayer.data.preferences

import com.dogeby.tagplayer.datastore.app.AppPreferencesData
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesData
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    val videoListPreferencesData: Flow<VideoListPreferencesData>

    val appPreferencesData: Flow<AppPreferencesData>

    suspend fun setTagFilter(tagIds: List<Long>)

    suspend fun setSortType(sortType: VideoListSortType)

    suspend fun setDirectoryFilter(directoryNames: List<String>)

    suspend fun setAppThemeMode(appThemeMode: AppThemeMode)

    suspend fun setAutoRotation(isAutoRotation: Boolean)

    suspend fun setRejectedUpdateVersionCode(versionCode: Int)
}
