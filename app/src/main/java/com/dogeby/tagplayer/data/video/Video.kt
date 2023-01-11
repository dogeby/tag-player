package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.database.model.VideoEntity

data class Video(
    val id: Long,
    val uri: String,
    val name: String,
    val extension: String,
    val duration: Int,
    val path: String,
)

fun Video.toVideoEntity() =
    VideoEntity(
        id = id,
        name = name,
        extension = extension,
        duration = duration,
        path = path
    )
