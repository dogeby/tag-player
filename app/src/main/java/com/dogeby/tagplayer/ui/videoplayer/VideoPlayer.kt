package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.ui.PlayerView
import com.dogeby.tagplayer.ui.theme.PlayerBackgroundColor

private const val POSITION_UPDATE_INTERVAL_MS = 200L

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    player: Player,
    uri: String,
    isPlaying: () -> Boolean,
    onPositionChanged: (Long) -> Unit,
    onRenderedFirstFrame: () -> Unit,
    onPlaybackStateChanged: (@Player.State Int) -> Unit,
    modifier: Modifier = Modifier,
    @Player.RepeatMode repeatMode: Int = REPEAT_MODE_ONE,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycleEvent by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    player.playWhenReady = isPlaying()

    DisposableEffect(player, uri, lifecycleOwner) {
        player.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            this.repeatMode = repeatMode
        }
        val observer = LifecycleEventObserver { _, event ->
            lifecycleEvent = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    AndroidView(
        factory = { androidViewContext ->
            PlayerView(androidViewContext).apply {
                useController = false
                setBackgroundColor(PlayerBackgroundColor.toArgb())

                fun getCurrentPosition() {
                    onPositionChanged(player.currentPosition)
                    if (player.isPlaying) this.postDelayed(::getCurrentPosition, POSITION_UPDATE_INTERVAL_MS)
                }
                val playerListener = object : Player.Listener {

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) postDelayed(::getCurrentPosition, POSITION_UPDATE_INTERVAL_MS)
                    }

                    override fun onRenderedFirstFrame() {
                        super.onRenderedFirstFrame()
                        onRenderedFirstFrame()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        onPositionChanged(player.currentPosition)
                        onPlaybackStateChanged(playbackState)
                    }
                }
                player.addListener(playerListener)

                this.player = player
            }
        },
        update = {
            if (it.player != player) it.player = player
            when (lifecycleEvent) {
                Lifecycle.Event.ON_STOP -> {
                    it.player?.pause()
                    it.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    it.onResume()
                    it.player?.playWhenReady = isPlaying()
                }
                else -> Unit
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}
