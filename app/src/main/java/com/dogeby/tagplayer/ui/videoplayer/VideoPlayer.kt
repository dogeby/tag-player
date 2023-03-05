package com.dogeby.tagplayer.ui.videoplayer

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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.PlayerBackgroundColor

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

    AndroidView(
        factory = {
            PlayerView(context).apply {
                useController = false
                setBackgroundColor(PlayerBackgroundColor.toArgb())
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
        modifier = modifier,
    )
}
