package com.dogeby.tagplayer.data.video.source.local

interface VideoLocalDataSource {

    suspend fun getVideoDataList(): Result<List<VideoData>>
}
