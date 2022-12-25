package com.dogeby.tagplayer.data.video.source.local

import kotlinx.coroutines.flow.StateFlow

interface VideoLocalDataSource {

    val videoDataList: StateFlow<List<VideoData>>

    suspend fun updateVideoDataList(): Result<Unit?>
}
