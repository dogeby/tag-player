package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject

class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
) {

    suspend operator fun invoke(name: String) {
        tagRepository.addTags(listOf(Tag(name = name)))
    }
}
