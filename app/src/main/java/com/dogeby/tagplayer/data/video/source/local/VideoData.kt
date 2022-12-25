package com.dogeby.tagplayer.data.video.source.local

data class VideoData(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentDirectory: String,
    val size: Int,
)
