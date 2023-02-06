package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.TagRepository
import javax.inject.Inject

class ModifyTagNameUseCase @Inject constructor(
    private val tagRepository: TagRepository,
) {

    suspend operator fun invoke(id: Long, name: String): Result<Unit> {
        return tagRepository.modifyTagName(id, name)
    }
}
