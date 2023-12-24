package com.dogeby.tagplayer.domain.backup

import com.dogeby.tagplayer.backup.BackupConverter
import com.dogeby.tagplayer.backup.model.BackupTagWithVideos
import com.dogeby.tagplayer.backup.model.BackupTags
import com.dogeby.tagplayer.backup.model.BackupVideo
import com.dogeby.tagplayer.domain.tag.GetTagItemsUseCase
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class EncodeTagsUseCase @Inject constructor(
    private val backupConverter: BackupConverter,
    private val getTagItemsUseCase: GetTagItemsUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<String> {
        return getTagItemsUseCase().mapLatest { tagItems ->
            val backupTagsWithVideos = tagItems.map { tagItem ->
                val videos = tagItem.videoItems.map {
                    BackupVideo(
                        name = it.name,
                        extension = it.extension,
                        size = it.size,
                    )
                }
                BackupTagWithVideos(
                    name = tagItem.name,
                    videos = videos,
                )
            }
            backupConverter.encodeBackupTags(BackupTags(backupTagsWithVideos))
        }
    }
}
