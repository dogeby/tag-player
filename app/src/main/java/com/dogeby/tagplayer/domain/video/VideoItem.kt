package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.util.VideoDuration

data class VideoItem(
    val id: Long,
    val uri: String,
    val name: String,
    val extension: String,
    val duration: VideoDuration,
    val formattedSize: String,
    val size: Long,
    val path: String,
    val parentDirectories: List<String>,
    val tags: List<Tag>,
)
