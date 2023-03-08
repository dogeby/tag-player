package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.PlayerBackgroundColor

private const val POSITION_UPDATE_INTERVAL_MS = 200L

@Composable
fun VideoPlayer(
    videoItem: VideoItem,
    isPlayWhenReady: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val videoPlayer = remember {
        ExoPlayer
            .Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    var lifecycleEvent by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentDuration by remember {
        mutableStateOf(VideoDuration(0))
    }

    DisposableEffect(videoItem.id, lifecycleOwner) {
        videoPlayer.apply {
            setMediaItem(MediaItem.fromUri(videoItem.uri))
            prepare()
        }
        val observer = LifecycleEventObserver { _, event ->
            lifecycleEvent = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            videoPlayer.release()
        }
    }
    Box(modifier = modifier) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    useController = false
                    setBackgroundColor(PlayerBackgroundColor.toArgb())
                    fun getCurrentPosition() {
                        val currentPosition = videoPlayer.currentPosition
                        if (currentDuration.value != currentPosition) currentDuration = VideoDuration(currentPosition)
                        if (videoPlayer.isPlaying) this.postDelayed(::getCurrentPosition, POSITION_UPDATE_INTERVAL_MS)
                    }
                    val playerListener = object : Player.Listener {

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            if (isPlaying) postDelayed(::getCurrentPosition, POSITION_UPDATE_INTERVAL_MS)
                        }
                    }
                    videoPlayer.addListener(playerListener)
                    player = videoPlayer
                }
            },
            update = {
                when (lifecycleEvent) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.player?.pause()
                        it.onPause()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        if (isPlayWhenReady) {
                            it.onResume()
                            it.player?.playWhenReady = true
                        }
                    }
                    else -> Unit
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        VideoPlayerController(
            videoItem = videoItem,
            currentDuration = currentDuration,
            totalDuration = videoItem.duration,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        )
    }
}
