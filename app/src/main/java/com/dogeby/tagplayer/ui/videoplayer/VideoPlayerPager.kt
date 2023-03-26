package com.dogeby.tagplayer.ui.videoplayer

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.VideoThumbnail
import com.dogeby.tagplayer.ui.component.toPx
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
                videoItem = { videoItem },
                isSettledPage = { videoItem.id == settledPageVideoId },
                onControllerVisibleChanged = onControllerVisibleChanged,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun VideoPlayerPage(
    videoItem: () -> VideoItem,
    isSettledPage: () -> Boolean,
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
    var playBackState by remember {
        mutableStateOf<@Player.State Int>(player.playbackState)
    }

    val videoItemValue = videoItem()
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
        if (isSettledPage()) {
            VideoPlayer(
                player = player,
                uri = videoItemValue.uri,
                isPlaying = { isPlaying },
                onPositionChanged = { currentDuration = it },
                onRenderedFirstFrame = { thumbnailVisible = false },
                onPlaybackStateChanged = { playBackState = it }
            )
        }
        if (thumbnailVisible) {
            val configuration = LocalConfiguration.current
            VideoThumbnail(
                uri = videoItemValue.uri,
                width = configuration.screenWidthDp.dp.toPx(),
                height = configuration.screenHeightDp.dp.toPx(),
                modifier = Modifier.fillMaxSize(),
                backgroundColor = PlayerBackgroundColor,
            )
        }

        VideoPlayerController(
            isVisible = controllerVisible,
            videoItem = videoItemValue,
            currentDuration = { VideoDuration(currentDuration) },
            totalDuration = videoItemValue.duration,
            isProgressBarExternalUpdate = { playBackState == Player.STATE_READY },
            isPlaying = { isPlaying },
            onPlay = { isPlaying = true },
            onPause = { isPlaying = false },
            onProgressBarScrubbingFinished = { player.seekTo(it.coerceIn(0, videoItemValue.duration.value)) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
