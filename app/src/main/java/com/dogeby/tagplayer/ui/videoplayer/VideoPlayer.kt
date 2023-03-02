package com.dogeby.tagplayer.ui.videoplayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.dogeby.tagplayer.domain.video.VideoItem

@Composable
fun VideoPlayer(
    videoItem: VideoItem,
    playWhenReady: Boolean,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: Context = LocalContext.current,
) {
    val videoPlayer = remember {
        ExoPlayer
            .Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(videoItem.uri))
                prepare()
            }
    }
    videoPlayer.playWhenReady = playWhenReady

    DisposableEffect(lifecycleOwner) {
        onDispose {
            videoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                useController = false
                player = videoPlayer
            }
        },
        modifier = modifier,
    )
}
