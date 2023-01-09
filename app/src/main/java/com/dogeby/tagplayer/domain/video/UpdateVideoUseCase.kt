package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject

class UpdateVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return videoRepository.updateVideos()
    }
}
