package com.dogeby.tagplayer.ui.videoplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.video.GetVideoItemByIdsUseCase
import com.dogeby.tagplayer.ui.navigation.VideoPlayerStartVideoId
import com.dogeby.tagplayer.ui.navigation.VideoPlayerVideoIdsArgument
import com.google.gson.Gson
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

    private val startVideoId: Long = checkNotNull(savedStateHandle[VideoPlayerStartVideoId])
    private val videoIds: List<Long> = Gson().fromJson(
        checkNotNull<String>(savedStateHandle[VideoPlayerVideoIdsArgument]),
        LongArray::class.java,
    ).toList()

    private val currentPageVideoId: StateFlow<Long> = savedStateHandle.getStateFlow(CURRENT_PAGE_VIDEO_ID, startVideoId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoPlayerPagerUiState: StateFlow<VideoPlayerPagerUiState> = getVideoItemByIdsUseCase(videoIds)
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
