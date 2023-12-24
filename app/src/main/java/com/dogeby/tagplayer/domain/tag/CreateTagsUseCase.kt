package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject

class CreateTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository,
) {

    suspend operator fun invoke(names: List<String>): List<Long> {
        val tags = names.map { Tag(name = it) }
        return tagRepository.addTags(tags)
    }
}
