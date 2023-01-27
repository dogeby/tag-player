package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject

class RemoveTagFromVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {

    suspend operator fun invoke(tagId: Long, videoIds: List<Long>) {
        videoRepository.removeTagFromVideos(tagId, videoIds)
    }
}
