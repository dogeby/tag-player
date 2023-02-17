package com.dogeby.tagplayer.domain.preferences

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class AddTagFilterUseCase @Inject constructor(
    private val getFilteredTagIdUseCase: GetFilteredTagIdUseCase,
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(tagId: Long) {
        val filteredTag = getFilteredTagIdUseCase().first()
        preferencesRepository.setTagFilter(filteredTag + tagId)
    }
}
