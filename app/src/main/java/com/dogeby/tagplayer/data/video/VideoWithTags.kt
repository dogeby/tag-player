package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.video.VideoItem

data class VideoWithTags(
    val video: Video,
    val tags: List<Tag>,
)

fun VideoWithTags.toVideoItem(duration: String, formattedSize: String) =
    with(video) {
        VideoItem(
            id = id,
            uri = uri,
            name = name,
            extension = extension,
            duration = duration,
            formattedSize = formattedSize,
            size = size,
            path = path,
            parentDirectories = path.split('/').filter { it.isNotBlank() },
            tags = tags,
        )
    }
