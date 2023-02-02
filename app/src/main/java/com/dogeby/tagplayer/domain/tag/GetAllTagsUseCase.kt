package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    operator fun invoke(): Flow<List<Tag>> {
        return tagRepository.allTags
    }
}
