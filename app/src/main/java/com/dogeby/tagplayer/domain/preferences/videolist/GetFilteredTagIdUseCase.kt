package com.dogeby.tagplayer.domain.preferences.videolist

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFilteredTagIdUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    operator fun invoke(): Flow<List<Long>> {
        return preferencesRepository.videoListPreferencesData.map {
            it.filteredTagIds
        }
    }
}
