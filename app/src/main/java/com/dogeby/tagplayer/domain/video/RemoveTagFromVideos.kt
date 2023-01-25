package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject

class RemoveTagFromVideos @Inject constructor(
    private val videoRepository: VideoRepository
) {

    suspend operator fun invoke(tagId: Long, videoIds: List<Long>) {
        videoRepository.removeTagFromVideos(tagId, videoIds)
    }
}
