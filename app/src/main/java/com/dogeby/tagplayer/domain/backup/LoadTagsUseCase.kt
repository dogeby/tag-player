package com.dogeby.tagplayer.domain.backup

import com.dogeby.tagplayer.backup.BackupConverter
import com.dogeby.tagplayer.backup.model.BackupVideo
import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.domain.tag.AddTagToVideosUseCase
import com.dogeby.tagplayer.domain.tag.CreateTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetTagItemsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class LoadTagsUseCase @Inject constructor(
    private val backupConverter: BackupConverter,
    private val videoRepository: VideoRepository,
    private val getTagItemsUseCase: GetTagItemsUseCase,
    private val createTagsUseCase: CreateTagsUseCase,
    private val addTagToVideosUseCase: AddTagToVideosUseCase,
) {

    suspend operator fun invoke(backupTagsJson: String): Result<Unit> = runCatching {
        val backupTags = backupConverter.decodeBackupTags(backupTagsJson)
        createTagsUseCase(backupTags.tags.map { it.name })
        val tagItems = getTagItemsUseCase.invoke().first().associateBy { it.name }
        videoRepository.updateVideos()
        val videos = videoRepository.videos.first().associateBy { video ->
            BackupVideo(
                name = video.name,
                extension = video.extension,
                size = video.size,
            )
        }
        backupTags.tags.forEach { backupTag ->
            val videoIds = backupTag.videos.mapNotNull { backupVideo ->
                val video = videos[backupVideo] ?: return@mapNotNull null
                video.id
            }
            if (videoIds.isNotEmpty()) tagItems[backupTag.name]?.let {
                addTagToVideosUseCase(
                    tagId = it.id,
                    videoIds = videoIds,
                )
            }
        }
    }
}
