package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllDirectoriesUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Set<String>> {
        return videoRepository.videosWithTags.mapLatest { videosWithTags ->
            videosWithTags.map { videoWithTags ->
                videoWithTags.video.path.split('/').filter { it.isNotBlank() }
            }.flatten().toSet()
        }
    }
}
