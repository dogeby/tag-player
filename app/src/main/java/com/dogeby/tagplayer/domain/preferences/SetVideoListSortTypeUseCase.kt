package com.dogeby.tagplayer.domain.preferences

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import javax.inject.Inject

class SetVideoListSortTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(sortType: VideoListSortType) {
        preferencesRepository.setSortType(sortType)
    }
}
