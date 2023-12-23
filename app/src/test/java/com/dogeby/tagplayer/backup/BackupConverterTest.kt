package com.dogeby.tagplayer.backup

import com.dogeby.tagplayer.backup.di.BackupModule
import com.dogeby.tagplayer.backup.model.BackupTagWithVideos
import com.dogeby.tagplayer.backup.model.BackupTags
import com.dogeby.tagplayer.backup.model.BackupVideo
import org.junit.Assert
import org.junit.Test

class BackupConverterTest {

    private val backupConverter = BackupConverter(BackupModule.providesJson())

    private val sampleBackupTags = BackupTags(
        List(2) { index ->
            BackupTagWithVideos("$index", List(1) { BackupVideo("$it", "$it", it.toLong()) })
        },
    )
    private val sampleBackupTagsJson = "{\"tags\":[{\"name\":\"0\",\"videos\":[{\"name\":\"0\",\"extension\":\"0\",\"size\":0}]},{\"name\":\"1\",\"videos\":[{\"name\":\"0\",\"extension\":\"0\",\"size\":0}]}]}"

    @Test
    fun encodeBackupTags() {
        val json = backupConverter.encodeBackupTags(sampleBackupTags)
        Assert.assertEquals(sampleBackupTagsJson, json)
    }

    @Test
    fun decodeBackupTags() {
        val backupTags = backupConverter.decodeBackupTags(sampleBackupTagsJson)
        Assert.assertEquals(sampleBackupTags, backupTags)
    }
}
