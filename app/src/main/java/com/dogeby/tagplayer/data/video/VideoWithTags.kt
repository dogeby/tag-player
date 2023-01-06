package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.video.VideoItem

data class VideoWithTags(
    val video: Video,
    val tags: List<Tag>,
)

fun VideoWithTags.toVideoItem(duration: String) =
    with(video) {
        VideoItem(
            id = id,
            uri = uri,
            name = name,
            duration = duration,
            tags = tags,
        )
    }
