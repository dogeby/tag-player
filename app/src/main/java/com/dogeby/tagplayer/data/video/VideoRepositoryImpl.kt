package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.toVideo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoRemoteDataSource: VideoLocalDataSource,
) : VideoRepository {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    override val videos: StateFlow<List<Video>> = _videos

    override suspend fun updateVideos(): Result<Unit> = runCatching {
        videoRemoteDataSource.getVideoDataList().onSuccess { videoDataList ->
            _videos.value = videoDataList.map { it.toVideo() }
        }
    }
}
