package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.util.VideoDuration

data class VideoWithTags(
    val video: Video,
    val tags: List<Tag>,
)

fun VideoWithTags.toVideoItem(formattedSize: String) =
    with(video) {
        VideoItem(
            id = id,
            uri = uri,
            name = name,
            extension = extension,
            duration = VideoDuration(duration),
            formattedSize = formattedSize,
            size = size,
            path = path,
            parentDirectories = path.split('/').filter { it.isNotBlank() },
            tags = tags,
        )
    }
