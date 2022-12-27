package com.dogeby.tagplayer.data.video

data class Video(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentDirectory: String,
)
