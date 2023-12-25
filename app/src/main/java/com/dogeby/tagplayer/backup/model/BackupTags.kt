package com.dogeby.tagplayer.backup.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupTags(
    val tags: List<BackupTagWithVideos>
)
