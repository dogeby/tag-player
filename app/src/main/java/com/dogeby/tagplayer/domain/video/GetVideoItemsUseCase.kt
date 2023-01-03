package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoWithTags
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetVideoItemsUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val formatDurationUseCase: FormatDurationUseCase,
) {

    operator fun invoke(tagIdsToFilter: List<Long>): Flow<List<VideoItem>> {
        val videosWithTags = if (tagIdsToFilter.isEmpty()) videoRepository.videosWithTags
        else videoRepository.getVideosWithTagsFilteredByTag(tagIdsToFilter)
        return videosWithTags.mapToVideoItems()
    }

    private fun Flow<List<VideoWithTags>>.mapToVideoItems(): Flow<List<VideoItem>> =
        map { videosWithTags ->
            videosWithTags.map {
                VideoItem(
                    id = it.video.id,
                    name = it.video.name,
                    duration = formatDurationUseCase(it.video.duration),
                    tags = it.tags,
                )
            }
        }
}
