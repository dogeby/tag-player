package com.dogeby.tagplayer.data.video.source

data class Video(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentDirectory: String,
)
