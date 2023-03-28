package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoUpdateResult
import com.dogeby.tagplayer.domain.preferences.GetFilteredDirectoryNameUseCase
import com.dogeby.tagplayer.domain.preferences.RemoveDirectoryFilterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class UpdateVideoListUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val getAllDirectoriesUseCase: GetAllDirectoriesUseCase,
    private val getFilteredDirectoryNameUseCase: GetFilteredDirectoryNameUseCase,
    private val removeDirectoryFilterUseCase: RemoveDirectoryFilterUseCase,
) {

    suspend operator fun invoke(): Result<Unit> {
        return videoRepository.updateVideos().onSuccess { videoUpdateResult ->
            when (videoUpdateResult) {
                VideoUpdateResult.Nothing -> Unit
                VideoUpdateResult.Cached -> {
                    val allDirectories = getAllDirectoriesUseCase().first()
                    val filteredDirectoryNames = getFilteredDirectoryNameUseCase().first()
                    removeDirectoryFilterUseCase(filteredDirectoryNames - allDirectories)
                }
            }
        }.map {}
    }
}
