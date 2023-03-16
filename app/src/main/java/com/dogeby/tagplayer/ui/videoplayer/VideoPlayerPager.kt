package com.dogeby.tagplayer.ui.videoplayer

import android.content.pm.ActivityInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.findActivity

@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerPager(
    currentPageVideoId: Long,
    videoItems: List<VideoItem>,
    onSettledPageChanged: (videoId: Long) -> Unit,
    onControllerVisibleChanged: (Boolean) -> Unit,
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

    var orientation by rememberSaveable {
        mutableStateOf(ActivityInfo.SCREEN_ORIENTATION_LOCKED)
    }
    ScreenRotation(orientation)

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
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
                isScreenLockRotation = orientation == ActivityInfo.SCREEN_ORIENTATION_LOCKED,
                onScreenUserRotation = { orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR },
                onScreenLockRotation = { orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED },
                onControllerVisibleChanged = onControllerVisibleChanged,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ScreenRotation(orientation: Int) {
    val activity = LocalContext.current.findActivity()
    activity?.requestedOrientation = orientation
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
