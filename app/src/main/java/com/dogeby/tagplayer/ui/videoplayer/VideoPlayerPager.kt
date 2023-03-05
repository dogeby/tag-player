package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.dogeby.tagplayer.domain.video.VideoItem

@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerPager(
    currentPageVideoId: Long,
    videoItems: List<VideoItem>,
    onSettledPageChanged: (videoId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        videoItems
            .indexOfFirst { it.id == currentPageVideoId }
            .coerceAtLeast(0),
    )
    val settledPageVideoId by remember {
        derivedStateOf {
            videoItems[pagerState.settledPage].id
        }
    }
    onSettledPageChanged(settledPageVideoId)

    VerticalPager(
        pageCount = videoItems.size,
        state = pagerState,
        modifier = modifier,
        key = { videoItems[it].id }
    ) { index ->
        val videoPlayerItem = videoItems[index]
        VideoPlayer(
            videoItem = videoPlayerItem,
            isPlayWhenReady = videoPlayerItem.id == settledPageVideoId,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
