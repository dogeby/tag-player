package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.domain.video.VideoItem

data class TagItem(
    val id: Long,
    val name: String,
    val videoItems: List<VideoItem>,
)
