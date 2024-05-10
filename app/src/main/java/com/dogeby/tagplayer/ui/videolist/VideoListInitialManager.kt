package com.dogeby.tagplayer.ui.videolist

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

class VideoListInitialManager(
    private val savedStateHandle: SavedStateHandle,
) {

    val videoListInitialItemIndex: StateFlow<Int> = savedStateHandle
        .getStateFlow(VIDEO_LIST_INITIAL_ITEM_INDEX, 0)

    fun setVideoListInitialItemIndex(index: Int) {
        savedStateHandle[VIDEO_LIST_INITIAL_ITEM_INDEX] = index
    }

    private companion object {
        const val VIDEO_LIST_INITIAL_ITEM_INDEX =
            "videoListInitialItemIndex"
    }
}
