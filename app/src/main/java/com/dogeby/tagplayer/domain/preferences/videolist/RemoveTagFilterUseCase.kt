package com.dogeby.tagplayer.domain.preferences.videolist

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class RemoveTagFilterUseCase @Inject constructor(
    private val getFilteredTagIdUseCase: GetFilteredTagIdUseCase,
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(tagId: Long) {
        val filteredTag = getFilteredTagIdUseCase().first()
        preferencesRepository.setTagFilter(filteredTag - tagId)
    }

    suspend operator fun invoke(tagIds: List<Long>) {
        val filteredTag = getFilteredTagIdUseCase().first()
        preferencesRepository.setTagFilter(filteredTag - tagIds.toSet())
    }
}
