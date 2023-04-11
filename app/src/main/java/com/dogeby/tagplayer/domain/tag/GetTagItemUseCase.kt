package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.TagRepository
import com.dogeby.tagplayer.domain.video.GetVideoItemByIdsUseCase
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

class GetTagItemUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val getVideoItemByIdsUseCase: GetVideoItemByIdsUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Long): Flow<Result<TagItem>> {
        return tagRepository.getTagWithVideoIds(id).mapLatest { result ->
            result.mapCatching { tagWithVideoIds ->
                TagItem(
                    id = tagWithVideoIds.tag.id,
                    name = tagWithVideoIds.tag.name,
                    videoItems = getVideoItemByIdsUseCase(tagWithVideoIds.videoIds).first().sortedBy { it.name }
                )
            }
        }
    }
}
