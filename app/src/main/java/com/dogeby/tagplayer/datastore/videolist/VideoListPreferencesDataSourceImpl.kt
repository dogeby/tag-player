package com.dogeby.tagplayer.datastore.videolist

import androidx.datastore.core.DataStore
import com.dogeby.tagplayer.VideoListPreferences
import com.dogeby.tagplayer.copy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class VideoListPreferencesDataSourceImpl @Inject constructor(
    private val videoListSettingPreferences: DataStore<VideoListPreferences>
) : VideoListPreferencesDataSource {

    override val videoListPreferencesData: Flow<VideoListPreferencesData> = videoListSettingPreferences.data
        .map {
            VideoListPreferencesData(
                filteredTagIds = it.filteredTagIdsList
            )
        }

    override suspend fun setFilteredTagIds(tagIds: Set<Long>) {
        videoListSettingPreferences.updateData {
            it.copy {
                filteredTagIds.clear()
                filteredTagIds.addAll(tagIds)
            }
        }
    }
}
