package com.dogeby.tagplayer.domain.video

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoWithTags
import com.dogeby.tagplayer.data.video.toVideoItem
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class GetVideoItemsUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<VideoItem>> {
        return preferencesRepository.videoListPreferencesData.flatMapLatest { videoListPreferencesData ->
            val filteredTagIds = videoListPreferencesData.filteredTagIds
            val sortType = videoListPreferencesData.sortType
            val videosWithTags = if (filteredTagIds.isEmpty()) {
                videoRepository.videosWithTags
            } else {
                videoRepository.getVideosWithTagsFilteredByTag(filteredTagIds)
            }

            videosWithTags.mapLatest { videos ->
                videos
                    .sorted(sortType)
                    .map {
                        it.toVideoItem()
                    }
                    .filterDirectoryFilter(videoListPreferencesData.filteredDirectoryNames.toSet())
            }
        }
    }

    private fun List<VideoWithTags>.sorted(videoListSortType: VideoListSortType): List<VideoWithTags> {
        return when (videoListSortType) {
            VideoListSortType.TITLE -> {
                sortedBy {
                    it.video.name
                }
            }
            VideoListSortType.TITLE_DESC -> {
                sortedByDescending {
                    it.video.name
                }
            }
            VideoListSortType.SIZE -> {
                sortedBy {
                    it.video.size
                }
            }
            VideoListSortType.SIZE_DESC -> {
                sortedByDescending {
                    it.video.size
                }
            }
            VideoListSortType.DURATION -> {
                sortedBy {
                    it.video.duration
                }
            }
            VideoListSortType.DURATION_DESC -> {
                sortedByDescending {
                    it.video.duration
                }
            }
        }
    }

    private fun List<VideoItem>.filterDirectoryFilter(filteredDirectoryNames: Set<String>): List<VideoItem> {
        if (filteredDirectoryNames.isEmpty()) return this
        return filter { videoItem -> videoItem.parentDirectories.any { it in filteredDirectoryNames } }
    }
}
