package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoWithTags
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GetVideoItemsUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val preferencesRepository: PreferencesRepository,
    private val formatDurationUseCase: FormatDurationUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<VideoItem>> {
        return preferencesRepository.videoListPreferencesData.flatMapLatest { videoListPreferencesData ->
            val filteredTagIds = videoListPreferencesData.filteredTagIds
            val videosWithTags = if (filteredTagIds.isEmpty()) {
                videoRepository.videosWithTags
            } else {
                videoRepository.getVideosWithTagsFilteredByTag(filteredTagIds)
            }

            videosWithTags.mapToVideoItems()
        }
    }

    private fun Flow<List<VideoWithTags>>.mapToVideoItems(): Flow<List<VideoItem>> =
        map { videosWithTags ->
            videosWithTags.map {
                VideoItem(
                    id = it.video.id,
                    name = it.video.name,
                    duration = formatDurationUseCase(it.video.duration),
                    tags = it.tags,
                )
            }
        }
}
