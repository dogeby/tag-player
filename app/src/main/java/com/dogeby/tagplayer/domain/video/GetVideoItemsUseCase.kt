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

    suspend operator fun invoke(tagIdsToFilter: List<Long>): Flow<List<VideoItem>> {
        return videoRepository.getVideosWithTagsFilteredByTag(tagIdsToFilter).mapToVideoItems()
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
