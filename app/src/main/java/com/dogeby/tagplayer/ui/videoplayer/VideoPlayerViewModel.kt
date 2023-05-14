package com.dogeby.tagplayer.ui.videoplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.video.GetVideoItemByIdsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    getVideoItemByIdsUseCase: GetVideoItemByIdsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val videoPlayerArgs = VideoPlayerArgs(savedStateHandle)

    private val currentPageVideoId: StateFlow<Long> = savedStateHandle.getStateFlow(CURRENT_PAGE_VIDEO_ID, videoPlayerArgs.startVideoId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoPlayerPagerUiState: StateFlow<VideoPlayerPagerUiState> = getVideoItemByIdsUseCase(videoPlayerArgs.videoIds)
        .mapLatest { videoItems ->
            if (videoItems.isEmpty()) return@mapLatest VideoPlayerPagerUiState.Empty
            VideoPlayerPagerUiState.Success(currentPageVideoId.value, videoItems)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoPlayerPagerUiState.Loading,
        )

    fun onPlayerSettledPageChanged(videoId: Long) {
        saveCurrentPageVideoId(videoId)
    }

    private fun saveCurrentPageVideoId(id: Long) {
        savedStateHandle[CURRENT_PAGE_VIDEO_ID] = id
    }

    companion object {

        private const val CURRENT_PAGE_VIDEO_ID = "currentPageVideoId"
    }
}
