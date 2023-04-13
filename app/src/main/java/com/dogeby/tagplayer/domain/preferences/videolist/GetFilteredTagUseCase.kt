package com.dogeby.tagplayer.domain.preferences.videolist

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class GetFilteredTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Tag>> {
        return preferencesRepository.videoListPreferencesData.flatMapLatest {
            tagRepository.getTags(it.filteredTagIds)
        }
    }
}
