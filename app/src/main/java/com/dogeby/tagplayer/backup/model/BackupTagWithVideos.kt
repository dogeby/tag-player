package com.dogeby.tagplayer.backup.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupTagWithVideos(
    val name: String,
    val videos: List<BackupVideo>,
)
