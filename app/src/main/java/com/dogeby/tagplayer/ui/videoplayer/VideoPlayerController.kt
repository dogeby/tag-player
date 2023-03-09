package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.PlayerControllerOnColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarIndicatorColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarTrackColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoPlayerController(
    videoItem: VideoItem,
    currentDuration: VideoDuration,
    totalDuration: VideoDuration,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                VideoPlayerVideoName(
                    name = videoItem.name,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 8.dp),
                )
                VideoPlayerDuration(
                    currentDuration = currentDuration.toString(),
                    totalDuration = totalDuration.toString(),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            VideoPlayerRightController(
                isPlaying = isPlaying,
                onPlay = onPlay,
                onPause = onPause,
                modifier = Modifier
            )
        }

        VideoPlayerProgressBar(
            currentDuration = currentDuration.value,
            totalDuration = totalDuration.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
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
        color = PlayerControllerOnColor,
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
        color = PlayerControllerOnColor,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
fun VideoPlayerProgressBar(
    currentDuration: Long,
    totalDuration: Long,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = currentDuration / totalDuration.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier,
        color = PlayerProgressBarIndicatorColor,
        trackColor = PlayerProgressBarTrackColor,
    )
}

@Composable
fun VideoPlayerRightController(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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
                tint = PlayerControllerOnColor,
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
            tint = PlayerControllerOnColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerControllerPreview() {
    TagPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Box {
                VideoPlayerController(
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
                    onPlay = {},
                    onPause = {},
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
                    currentDuration = 30,
                    totalDuration = 60,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
