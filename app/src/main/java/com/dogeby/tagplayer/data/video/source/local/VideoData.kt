package com.dogeby.tagplayer.data.video.source.local

import com.dogeby.tagplayer.data.video.Video

data class VideoData(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentFolder: String,
    val size: Int,
)

fun VideoData.toVideo() = Video(
    id = id,
    name = name,
    duration = duration,
    parentDirectory = parentFolder,
)
