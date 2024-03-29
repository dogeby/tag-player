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
                filteredTagIds = it.filteredTagIdsList,
                filteredDirectoryNames = it.filteredDirectoryNamesList,
                sortType = VideoListSortType.values()[it.sortType],
            )
        }

    override suspend fun setTagFilter(tagIds: List<Long>) {
        videoListSettingPreferences.updateData {
            it.copy {
                filteredTagIds.clear()
                filteredTagIds.addAll(tagIds)
            }
        }
    }

    override suspend fun setSortType(sortType: VideoListSortType) {
        videoListSettingPreferences.updateData { videoListPreferences ->
            videoListPreferences.toBuilder()
                .setSortType(sortType.ordinal)
                .build()
        }
    }

    override suspend fun setDirectoryFilter(directoryNames: List<String>) {
        videoListSettingPreferences.updateData {
            it.copy {
                filteredDirectoryNames.clear()
                filteredDirectoryNames.addAll(directoryNames)
            }
        }
    }
}
