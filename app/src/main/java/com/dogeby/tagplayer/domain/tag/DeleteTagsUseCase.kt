package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject

class DeleteTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    suspend operator fun invoke(ids: List<Long>) {
        tagRepository.deleteTags(ids)
    }
}
