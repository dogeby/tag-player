package com.dogeby.tagplayer.domain.preferences

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetVideoListSortTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<VideoListSortType> {
        return preferencesRepository.videoListPreferencesData.mapLatest {
            it.sortType
        }
    }
}
