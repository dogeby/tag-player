package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.toVideoItem
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetVideoItemByIdsUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val formatDurationUseCase: FormatDurationUseCase,
    private val formatSizeUseCase: FormatSizeUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(videoIds: List<Long>): Flow<List<VideoItem>> {
        return videoRepository.getVideosWithTags(videoIds).mapLatest { videos ->
            videos.map {
                it.toVideoItem(
                    duration = formatDurationUseCase(it.video.duration),
                    formattedSize = formatSizeUseCase(it.video.size),
                )
            }
        }
    }
}
