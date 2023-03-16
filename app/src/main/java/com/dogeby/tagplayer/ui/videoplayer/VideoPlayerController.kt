package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring.DampingRatioNoBouncy
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.PlayerControllerOnBackgroundColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarIndicatorColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarTrackColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoPlayerController(
    isVisible: Boolean,
    videoItem: VideoItem,
    currentDuration: VideoDuration,
    totalDuration: VideoDuration,
    isPlaying: Boolean,
    isLoading: Boolean,
    isScreenLockRotation: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onScreenUserRotation: () -> Unit,
    onScreenLockRotation: () -> Unit,
    onProgressBarChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    VideoPlayerControllerAnimation(
        visible = isVisible,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.videoPlayerController_text_horizontal_padding)),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                VideoPlayerVideoName(
                    name = videoItem.name,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterVertically),
                )
                VideoPlayerRightController(
                    isPlaying = isPlaying,
                    isScreenLockRotation = isScreenLockRotation,
                    onPlay = onPlay,
                    onPause = onPause,
                    onScreenUserRotation = onScreenUserRotation,
                    onScreenLockRotation = onScreenLockRotation,
                )
            }

            VideoPlayerProgressBar(
                currentDuration = currentDuration,
                totalDuration = totalDuration,
                isLoading = isLoading,
                onScrubbingFinished = { onProgressBarChanged(it.value) },
                modifier = Modifier.fillMaxWidth(),
                durationTextPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.videoPlayerController_text_horizontal_padding)),
            )
        }
    }
}

@Composable
fun VideoPlayerControllerAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        content()
    }
}

@Composable
fun VideoPlayerVideoName(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = name,
        modifier = modifier,
        color = PlayerControllerOnBackgroundColor,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun VideoPlayerDuration(
    currentDuration: String,
    totalDuration: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "$currentDuration / $totalDuration",
        modifier = modifier,
        color = PlayerControllerOnBackgroundColor,
        style = MaterialTheme.typography.labelMedium,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerProgressBar(
    currentDuration: VideoDuration,
    totalDuration: VideoDuration,
    isLoading: Boolean,
    onScrubbingFinished: (VideoDuration) -> Unit,
    modifier: Modifier = Modifier,
    durationTextPadding: PaddingValues = PaddingValues(),
) {
    var isScrubbing by remember(isLoading) { mutableStateOf(false) }
    var sliderCurrentProgress by remember { mutableStateOf(currentDuration) }
    if (isScrubbing.not()) sliderCurrentProgress = currentDuration

    val animatedProgress by animateFloatAsState(
        targetValue = sliderCurrentProgress.value.toFloat(),
        animationSpec = if (isScrubbing) spring(DampingRatioNoBouncy, StiffnessHigh) else ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(modifier = modifier) {
        VideoPlayerDuration(
            currentDuration = sliderCurrentProgress.toString(),
            totalDuration = totalDuration.toString(),
            modifier = Modifier.padding(durationTextPadding)
        )
        Slider(
            value = animatedProgress,
            onValueChange = {
                isScrubbing = true
                sliderCurrentProgress = VideoDuration(it.toLong())
            },
            valueRange = 0f..totalDuration.value.toFloat(),
            colors = SliderDefaults.colors(
                activeTrackColor = PlayerProgressBarIndicatorColor,
                inactiveTrackColor = PlayerProgressBarTrackColor
            ),
            onValueChangeFinished = { onScrubbingFinished(sliderCurrentProgress) },
            thumb = {},
        )
    }
}

@Composable
fun VideoPlayerRightController(
    isPlaying: Boolean,
    isScreenLockRotation: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onScreenUserRotation: () -> Unit,
    onScreenLockRotation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LocalConfiguration.current.orientation
    Column(modifier = modifier) {
        ScreenRotationButton(
            isScreenLockRotation = isScreenLockRotation,
            onScreenUserRotation = onScreenUserRotation,
            onScreenLockRotation = onScreenLockRotation,
        )
        PlayPauseButton(
            isPlaying = isPlaying,
            onPlay = onPlay,
            onPause = onPause,
        )
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isPlaying) {
        IconButton(
            onClick = onPause,
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pause),
                contentDescription = null,
                tint = PlayerControllerOnBackgroundColor,
            )
        }
        return
    }
    IconButton(
        onClick = onPlay,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_play),
            contentDescription = null,
            tint = PlayerControllerOnBackgroundColor,
        )
    }
}

@Composable
fun ScreenRotationButton(
    isScreenLockRotation: Boolean,
    onScreenUserRotation: () -> Unit,
    onScreenLockRotation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isScreenLockRotation) {
        IconButton(
            onClick = onScreenUserRotation,
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_screen_rotation),
                contentDescription = null,
                tint = PlayerControllerOnBackgroundColor,
            )
        }
        return
    }
    IconButton(
        onClick = onScreenLockRotation,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_screen_lock_rotation),
            contentDescription = null,
            tint = PlayerControllerOnBackgroundColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerControllerPreview() {
    TagPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Box(modifier = Modifier.fillMaxSize()) {
                VideoPlayerController(
                    isVisible = true,
                    videoItem = VideoItem(
                        id = 0,
                        uri = "",
                        name = "video",
                        extension = "mp4",
                        duration = VideoDuration(10000),
                        formattedSize = "",
                        size = 0,
                        path = "",
                        parentDirectories = emptyList(),
                        tags = emptyList(),
                    ),
                    currentDuration = VideoDuration(20000),
                    totalDuration = VideoDuration(200000),
                    isPlaying = true,
                    isLoading = false,
                    isScreenLockRotation = false,
                    onPlay = {},
                    onPause = {},
                    onScreenUserRotation = {},
                    onScreenLockRotation = {},
                    onProgressBarChanged = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerDurationPreview() {
    TagPlayerTheme {
        Surface(color = Color.Black) {
            VideoPlayerDuration(currentDuration = "12:12", totalDuration = "24:30")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerProgressBarPreview() {
    TagPlayerTheme {
        Surface(color = Color.Black) {
            Box {
                VideoPlayerProgressBar(
                    currentDuration = VideoDuration(30),
                    totalDuration = VideoDuration(60),
                    isLoading = false,
                    onScrubbingFinished = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    durationTextPadding = PaddingValues(dimensionResource(id = R.dimen.videoPlayerController_text_horizontal_padding)),
                )
            }
        }
    }
}
