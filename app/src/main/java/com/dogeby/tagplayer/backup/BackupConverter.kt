package com.dogeby.tagplayer.backup

import com.dogeby.tagplayer.backup.model.BackupTags
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class BackupConverter @Inject constructor(
    private val json: Json,
) {

    fun encodeBackupTags(tags: BackupTags): String {
        return json.encodeToString(tags)
    }

    fun decodeBackupTags(backupTagsJson: String): BackupTags {
        return json.decodeFromString(backupTagsJson)
    }
}
