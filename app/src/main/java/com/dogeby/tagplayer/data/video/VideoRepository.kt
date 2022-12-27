package com.dogeby.tagplayer.data.video

import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    val videos: Flow<List<Video>>

    suspend fun updateVideos(): Result<Unit>
}
