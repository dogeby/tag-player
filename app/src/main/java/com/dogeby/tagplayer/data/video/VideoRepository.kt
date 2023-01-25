package com.dogeby.tagplayer.data.video

import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    val videos: Flow<List<Video>>

    val videosWithTags: Flow<List<VideoWithTags>>

    suspend fun updateVideos(): Result<Unit>

    fun getVideosWithTagsFilteredByTag(tagIds: List<Long>): Flow<List<VideoWithTags>>

    suspend fun addTagToVideos(tagId: Long, videoIds: List<Long>)
}
