package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.TagRepository
import com.dogeby.tagplayer.domain.preferences.videolist.RemoveTagFilterUseCase
import javax.inject.Inject

class DeleteTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val removeTagFilterUseCase: RemoveTagFilterUseCase,
) {

    suspend operator fun invoke(ids: List<Long>) {
        tagRepository.deleteTags(ids).onSuccess {
            removeTagFilterUseCase(ids)
        }
    }
}
