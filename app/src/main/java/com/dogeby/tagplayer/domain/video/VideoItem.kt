package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.tag.Tag

data class VideoItem(
    val id: Long,
    val uri: String,
    val name: String,
    val extension: String,
    val duration: String,
    val parentDirectories: List<String>,
    val tags: List<Tag>,
)
