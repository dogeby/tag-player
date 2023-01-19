package com.dogeby.tagplayer.domain.preferences

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetIsTagFilteredUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    operator fun invoke(): Flow<Boolean> {
        return preferencesRepository.videoListPreferencesData.map {
            it.filteredTagIds.isNotEmpty()
        }
    }
}
