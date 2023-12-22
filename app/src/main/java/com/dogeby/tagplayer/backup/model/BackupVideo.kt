package com.dogeby.tagplayer.backup.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupVideo(
    val name: String,
    val extension: String,
    val size: Long,
)
