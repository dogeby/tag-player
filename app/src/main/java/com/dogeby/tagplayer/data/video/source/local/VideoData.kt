package com.dogeby.tagplayer.data.video.source.local

import com.dogeby.tagplayer.data.video.Video
import com.dogeby.tagplayer.database.model.VideoEntity

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

fun VideoData.toVideoEntity() = VideoEntity(
    id = id,
    name = name,
    duration = duration,
    parentDirectory = parentFolder
)
