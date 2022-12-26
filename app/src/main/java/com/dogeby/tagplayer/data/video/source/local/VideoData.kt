package com.dogeby.tagplayer.data.video.source.local

data class VideoData(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentFolder: String,
    val size: Int,
)
