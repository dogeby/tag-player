package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.toVideoEntity
import com.dogeby.tagplayer.database.dao.TagVideoCrossRefDao
import com.dogeby.tagplayer.database.dao.VideoDao
import com.dogeby.tagplayer.database.model.TagVideoCrossRef
import com.dogeby.tagplayer.database.model.toVideo
import com.dogeby.tagplayer.database.model.toVideoWithTags
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val videoLocalDataSource: VideoLocalDataSource,
    private val videoDao: VideoDao,
    private val tagVideoCrossRefDao: TagVideoCrossRefDao,
) : VideoRepository {

    override val videos: Flow<List<Video>> = videoDao.getVideoEntities().map { videoEntities ->
        videoEntities.map { it.toVideo(it.id.toUri()) }
    }

    override val videosWithTags: Flow<List<VideoWithTags>> = tagVideoCrossRefDao.getVideosWithTagsFilteredNotByTag().map { videoEntityWithTagEntities ->
        videoEntityWithTagEntities.map { it.toVideoWithTags(it.videoEntity.id.toUri()) }
    }

    override fun getVideosWithTags(videoIds: List<Long>): Flow<List<VideoWithTags>> {
        return tagVideoCrossRefDao.getVideosWithTags(videoIds).map { videoEntityWithTagEntities ->
            videoEntityWithTagEntities.map { it.toVideoWithTags(it.videoEntity.id.toUri()) }
        }
    }

    override fun findVideosWithTags(nameKeyword: String): Flow<List<VideoWithTags>> {
        return tagVideoCrossRefDao.getVideosWithTags(nameKeyword).map { videoEntityWithTagEntities ->
            videoEntityWithTagEntities.map { it.toVideoWithTags(it.videoEntity.id.toUri()) }
        }
    }

    override suspend fun updateVideos(): Result<Unit> = runCatching {
        videoLocalDataSource.getVideoDataList().onSuccess { videoDataList ->
            videoDao.cacheVideos(videoDataList.map { it.toVideoEntity() })
        }
    }

    override fun getVideosWithTagsFilteredByTag(tagIds: List<Long>): Flow<List<VideoWithTags>> {
        return tagVideoCrossRefDao.getVideosWithTagsFilteredByTag(tagIds).map { videoEntityWithTagEntities ->
            videoEntityWithTagEntities.map { it.toVideoWithTags(it.videoEntity.id.toUri()) }
        }
    }

    private fun Long.toUri(): String {
        return "${videoLocalDataSource.contentUri}/$this"
    }

    override suspend fun addTagToVideos(tagId: Long, videoIds: List<Long>) {
        tagVideoCrossRefDao.insertTagVideoCrossRefs(
            videoIds.map { videoId -> TagVideoCrossRef(tagId, videoId) }
        )
    }

    override suspend fun removeTagFromVideos(tagId: Long, videoIds: List<Long>) {
        tagVideoCrossRefDao.deleteTagVideoCrossRefs(
            videoIds.map { videoId -> TagVideoCrossRef(tagId, videoId) }
        )
    }
}
