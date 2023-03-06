package com.dogeby.tagplayer.data.video.source.local

import com.dogeby.tagplayer.data.video.Video
import com.dogeby.tagplayer.database.model.VideoEntity

data class VideoData(
    val id: Long,
    val fileName: String,
    val duration: Long,
    val path: String,
    val size: Long,
)

fun VideoData.toVideo(uri: String) = Video(
    id = id,
    uri = uri,
    name = fileName.toName(),
    extension = fileName.toExtension(),
    duration = duration,
    path = path,
    size = size,
)

fun VideoData.toVideoEntity() = VideoEntity(
    id = id,
    name = fileName.toName(),
    extension = fileName.toExtension(),
    duration = duration,
    path = path,
    size = size,
)

private fun String.toName() = substringBeforeLast(".")

private fun String.toExtension() = substringAfterLast('.', "")
