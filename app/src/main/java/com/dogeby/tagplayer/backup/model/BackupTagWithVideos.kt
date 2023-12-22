package com.dogeby.tagplayer.backup.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupTagWithVideos(
    val tag: BackupTag,
    val videos: List<BackupVideo>,
)
