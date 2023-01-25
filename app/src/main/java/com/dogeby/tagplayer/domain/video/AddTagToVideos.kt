package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject

class AddTagToVideos @Inject constructor(
    private val videoRepository: VideoRepository
) {

    suspend operator fun invoke(tagId: Long, videoIds: List<Long>) {
        videoRepository.addTagToVideos(tagId, videoIds)
    }
}
