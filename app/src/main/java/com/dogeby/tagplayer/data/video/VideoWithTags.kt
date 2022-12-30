package com.dogeby.tagplayer.data.video

import com.dogeby.tagplayer.data.tag.Tag

data class VideoWithTags(
    val video: Video,
    val tags: List<Tag>
)
