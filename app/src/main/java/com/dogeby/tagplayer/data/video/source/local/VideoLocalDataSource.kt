package com.dogeby.tagplayer.data.video.source.local

interface VideoLocalDataSource {

    val contentUri: String

    suspend fun getVideoDataList(): Result<List<VideoData>>
}
