package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FindTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    operator fun invoke(nameKeyword: String): Flow<List<Tag>> {
        return tagRepository.findTags(nameKeyword)
    }
}
