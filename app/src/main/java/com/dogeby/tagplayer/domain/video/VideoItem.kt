package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.tag.Tag

data class VideoItem(
    val id: Long,
    val name: String,
    val duration: String,
    val tags: List<Tag>,
)
