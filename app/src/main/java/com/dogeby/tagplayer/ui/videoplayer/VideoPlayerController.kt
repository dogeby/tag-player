package com.dogeby.tagplayer.ui.videoplayer

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.provider.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.activity.findActivity
import com.dogeby.tagplayer.ui.permission.WriteSettingsPermissionCheck
import com.dogeby.tagplayer.ui.theme.PlayerControllerOnBackgroundColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarIndicatorColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarTrackColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoPlayerController(
    isVisible: () -> Boolean,
    videoItem: VideoItem,
    currentDuration: () -> VideoDuration,
    totalDuration: VideoDuration,
    isProgressBarExternalUpdate: () -> Boolean,
    isPlaying: () -> Boolean,
    orientation: () -> Int,
    useSystemAutoRotation: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onRotationBtnClick: (Int) -> Unit,
    onProgressBarScrubbingFinished: (Long) -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VideoPlayerControllerAnimation(
        visible = isVisible(),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
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
                        .align(Alignment.Bottom),
                )
                VideoPlayerRightController(
                    isPlaying = isPlaying,
                    orientation = orientation,
                    useSystemAutoRotation = useSystemAutoRotation,
                    onPlay = onPlay,
                    onPause = onPause,
                    onRotationBtnClick = onRotationBtnClick,
                    onSeekBack = onSeekBack,
                    onSeekForward = onSeekForward,
                )
            }

            VideoPlayerProgressBar(
                currentDuration = currentDuration,
                totalDuration = totalDuration,
                onScrubbingFinished = onProgressBarScrubbingFinished,
                isExternalUpdate = isProgressBarExternalUpdate,
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
    content: @Composable AnimatedVisibilityScope.() -> Unit,
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
    currentDuration: () -> VideoDuration,
    totalDuration: VideoDuration,
    onScrubbingFinished: (Long) -> Unit,
    isExternalUpdate: () -> Boolean,
    modifier: Modifier = Modifier,
    durationTextPadding: PaddingValues = PaddingValues(),
) {
    var isScrubbing by remember { mutableStateOf(false) }

    var sliderCurrentProgress by remember { mutableStateOf(currentDuration()) }
    if (isScrubbing.not() && isExternalUpdate()) { sliderCurrentProgress = currentDuration() }

    val animatedProgress by animateFloatAsState(
        targetValue = sliderCurrentProgress.value.toFloat(),
        animationSpec = if (isScrubbing || isExternalUpdate().not()) spring(DampingRatioNoBouncy, StiffnessHigh) else ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(modifier = modifier) {
        VideoPlayerDuration(
            currentDuration = sliderCurrentProgress.toString(),
            totalDuration = totalDuration.toString(),
            modifier = Modifier.padding(durationTextPadding),
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
                inactiveTrackColor = PlayerProgressBarTrackColor,
            ),
            onValueChangeFinished = {
                onScrubbingFinished(sliderCurrentProgress.value)
                isScrubbing = false
            },
            thumb = {},
        )
    }
}

@Composable
fun VideoPlayerRightController(
    isPlaying: () -> Boolean,
    orientation: () -> Int,
    useSystemAutoRotation: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onRotationBtnClick: (Int) -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        ScreenRotationButton(
            orientation = orientation,
            useSystemAutoRotation = useSystemAutoRotation,
            onClick = onRotationBtnClick,
        )
        PlayPauseButton(
            isPlaying = isPlaying,
            onPlay = onPlay,
            onPause = onPause,
        )
        PlayerSeekButtons(
            onSeekBack = onSeekBack,
            onSeekForward = onSeekForward
        )
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: () -> Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isPlaying()) {
        IconButton(
            onClick = onPause,
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
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
            painter = painterResource(id = R.drawable.ic_pause),
            contentDescription = null,
            tint = PlayerControllerOnBackgroundColor,
        )
    }
}

@Composable
fun ScreenRotationButton(
    orientation: () -> Int,
    useSystemAutoRotation: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isCheckWriteSettingsPermission by rememberSaveable { mutableStateOf(false) }

    if (isCheckWriteSettingsPermission) {
        WriteSettingsPermissionCheck(
            onDismiss = { isCheckWriteSettingsPermission = false },
        )
    }

    if (orientation() == ActivityInfo.SCREEN_ORIENTATION_LOCKED) {
        IconButton(
            onClick = {
                if (useSystemAutoRotation) {
                    if (Settings.System.canWrite(context).not()) {
                        isCheckWriteSettingsPermission = true
                        return@IconButton
                    }
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.ACCELEROMETER_ROTATION,
                        1,
                    )
                }
                onClick(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            },
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_screen_lock_rotation),
                contentDescription = null,
                tint = PlayerControllerOnBackgroundColor,
            )
        }
        return
    }
    IconButton(
        onClick = {
            if (useSystemAutoRotation) {
                if (Settings.System.canWrite(context).not()) {
                    isCheckWriteSettingsPermission = true
                    return@IconButton
                }
                val defaultDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.display
                } else {
                    context.findActivity()?.windowManager?.defaultDisplay
                }
                defaultDisplay?.let { display ->
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.USER_ROTATION,
                        display.rotation,
                    )
                }
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    0,
                )
            }
            onClick(ActivityInfo.SCREEN_ORIENTATION_LOCKED)
        },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_screen_rotation),
            contentDescription = null,
            tint = PlayerControllerOnBackgroundColor,
        )
    }
}

fun getCurrentScreenOrientation(
    context: Context,
    activity: Activity?,
    useSystemAutoRotation: Boolean,
): Int {
    return if (useSystemAutoRotation) {
        when (Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION)) {
            1 -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
            else -> ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
    } else {
        when (activity?.requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
            else -> ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
    }
}

@Composable
fun PlayerSeekButtons(
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        IconButton(onClick = onSeekBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_replay_5),
                contentDescription = null,
                tint = PlayerControllerOnBackgroundColor,
            )
        }
        IconButton(onClick = onSeekForward) {
            Icon(
                painter = painterResource(id = R.drawable.ic_forward_5),
                contentDescription = null,
                tint = PlayerControllerOnBackgroundColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerControllerPreview() {
    TagPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Box(modifier = Modifier.fillMaxSize()) {
                VideoPlayerController(
                    isVisible = { true },
                    videoItem = VideoItem(
                        id = 0,
                        uri = "",
                        name = "video",
                        extension = "mp4",
                        duration = VideoDuration(10000),
                        size = 0,
                        path = "",
                        parentDirectories = emptyList(),
                        tags = emptyList(),
                    ),
                    currentDuration = { VideoDuration(20000) },
                    totalDuration = VideoDuration(200000),
                    isProgressBarExternalUpdate = { true },
                    isPlaying = { true },
                    orientation = { ActivityInfo.SCREEN_ORIENTATION_SENSOR },
                    useSystemAutoRotation = false,
                    onPlay = {},
                    onPause = {},
                    onRotationBtnClick = {},
                    onProgressBarScrubbingFinished = {},
                    onSeekBack = {},
                    onSeekForward = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
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
                    currentDuration = { VideoDuration(30) },
                    totalDuration = VideoDuration(60),
                    onScrubbingFinished = {},
                    isExternalUpdate = { true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    durationTextPadding = PaddingValues(dimensionResource(id = R.dimen.videoPlayerController_text_horizontal_padding)),
                )
            }
        }
    }
}
