package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCommonTagsFromVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {

    operator fun invoke(videoIds: List<Long>): Flow<List<Tag>> {
        return videoRepository.getVideosWithTags(videoIds).map { videosWithTags ->
            videosWithTags
                .map { it.tags }
                .flatten()
                .groupingBy { it }
                .eachCount()
                .filter { it.value == videosWithTags.size }
                .keys
                .toList()
        }
    }
}
