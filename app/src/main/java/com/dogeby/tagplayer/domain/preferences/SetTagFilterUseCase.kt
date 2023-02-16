package com.dogeby.tagplayer.domain.preferences

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject

class SetTagFilterUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(tagIds: List<Long>) {
        preferencesRepository.setTagFilter(tagIds)
    }
}
