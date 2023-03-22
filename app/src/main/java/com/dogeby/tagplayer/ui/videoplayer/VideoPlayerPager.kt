package com.dogeby.tagplayer.ui.videoplayer

import android.content.pm.ActivityInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.VideoThumbnail
import com.dogeby.tagplayer.ui.component.toPx
import com.dogeby.tagplayer.ui.findActivity
import com.dogeby.tagplayer.ui.theme.PlayerBackgroundColor

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

    var orientation by remember {
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
            val videoItem = videoItems[index]
            VideoPlayerPage(
                videoItem = videoItem,
                isSettledPage = videoItem.id == settledPageVideoId,
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

@Composable
fun VideoPlayerPage(
    videoItem: VideoItem,
    isSettledPage: Boolean,
    isScreenLockRotation: Boolean,
    onScreenUserRotation: () -> Unit,
    onScreenLockRotation: () -> Unit,
    onControllerVisibleChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer
            .Builder(context)
            .build()
    }
    var isPlaying by remember {
        mutableStateOf(true)
    }
    var controllerVisible by remember {
        mutableStateOf(true).also {
            onControllerVisibleChanged(it.value)
        }
    }
    var currentDuration by remember {
        mutableStateOf(0L)
    }
    var thumbnailVisible by remember {
        mutableStateOf(true)
    }

    val playerInteractionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                interactionSource = playerInteractionSource,
                indication = null,
            ) {
                controllerVisible = controllerVisible.not()
                onControllerVisibleChanged(controllerVisible)
            },
    ) {
        if (isSettledPage) {
            VideoPlayer(
                player = player,
                uri = videoItem.uri,
                isPlaying = isPlaying,
                onPositionChanged = { currentDuration = it },
                onRenderedFirstFrame = { thumbnailVisible = false },
            )
        }
        if (thumbnailVisible) {
            val configuration = LocalConfiguration.current
            VideoThumbnail(
                uri = videoItem.uri,
                width = configuration.screenWidthDp.dp.toPx(),
                height = configuration.screenHeightDp.dp.toPx(),
                modifier = Modifier.fillMaxSize(),
                backgroundColor = PlayerBackgroundColor,
            )
        }

        VideoPlayerController(
            isVisible = controllerVisible,
            videoItem = videoItem,
            currentDuration = VideoDuration(currentDuration),
            totalDuration = videoItem.duration,
            isPlaying = isPlaying,
            isLoading = player.isLoading,
            isScreenLockRotation = isScreenLockRotation,
            onPlay = { isPlaying = true },
            onPause = { isPlaying = false },
            onScreenUserRotation = onScreenUserRotation,
            onScreenLockRotation = onScreenLockRotation,
            onProgressBarChangeFinished = { player.seekTo(it.coerceIn(0, videoItem.duration.value)) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
