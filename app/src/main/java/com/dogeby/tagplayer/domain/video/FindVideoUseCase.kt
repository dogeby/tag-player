package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.toVideoItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FindVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {

    operator fun invoke(query: String): Flow<List<VideoItem>> {
        return videoRepository.findVideosWithTags(query.trim()).map { videos ->
            videos.map {
                it.toVideoItem()
            }
        }
    }
}
