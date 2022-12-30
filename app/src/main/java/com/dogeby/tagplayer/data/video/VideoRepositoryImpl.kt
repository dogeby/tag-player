package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.toVideoEntity
import com.dogeby.tagplayer.database.dao.TagVideoCrossRefDao
import com.dogeby.tagplayer.database.dao.VideoDao
import com.dogeby.tagplayer.database.model.toVideo
import com.dogeby.tagplayer.database.model.toVideoWithTags
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoRemoteDataSource: VideoLocalDataSource,
    private val videoDao: VideoDao,
    private val tagVideoCrossRefDao: TagVideoCrossRefDao,
) : VideoRepository {

    override val videos: Flow<List<Video>> = videoDao.getVideoEntities().map { videoEntities ->
        videoEntities.map { it.toVideo() }
    }

    override val videosWithTags: Flow<List<VideoWithTags>> = tagVideoCrossRefDao.getVideosWithTags().map { videoEntityWithTagEntities ->
        videoEntityWithTagEntities.map { it.toVideoWithTags() }
    }

    override suspend fun updateVideos(): Result<Unit> = runCatching {
        videoRemoteDataSource.getVideoDataList().onSuccess { videoDataList ->
            videoDao.cacheVideos(videoDataList.map { it.toVideoEntity() })
        }
    }

    override suspend fun getVideosWithTagsFilteredByTag(tagIds: List<Long>): Flow<List<VideoWithTags>> {
        return tagVideoCrossRefDao.getVideosWithTagsFilteredByTag(tagIds).map { videoEntityWithTagEntities ->
            videoEntityWithTagEntities.map { it.toVideoWithTags() }
        }
    }
}
