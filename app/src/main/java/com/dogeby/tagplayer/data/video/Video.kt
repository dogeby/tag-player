package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.database.model.VideoEntity

data class Video(
    val id: Long,
    val name: String,
    val duration: Int,
    val parentDirectory: String,
)

fun Video.toVideoEntity() =
    VideoEntity(
        id = id,
        name = name,
        duration = duration,
        parentDirectory = parentDirectory
    )
