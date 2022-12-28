package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.toVideoEntity
import com.dogeby.tagplayer.database.dao.VideoDao
import com.dogeby.tagplayer.database.model.toVideo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoRemoteDataSource: VideoLocalDataSource,
    private val videoDao: VideoDao
) : VideoRepository {

    override val videos: Flow<List<Video>> = videoDao.getVideoEntities().map { videoEntities ->
        videoEntities.map { it.toVideo() }
    }

    override suspend fun updateVideos(): Result<Unit> = runCatching {
        videoRemoteDataSource.getVideoDataList().onSuccess { videoDataList ->
            videoDao.cacheVideos(videoDataList.map { it.toVideoEntity() })
        }
    }
}
