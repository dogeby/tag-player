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
import androidx.core.os.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.ui.PlayerView
import com.dogeby.tagplayer.ui.activity.findActivity
import com.dogeby.tagplayer.ui.activity.setKeepScreenOn
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

    DisposableEffect(player, uri, lifecycleOwner) {
        player.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            this.repeatMode = repeatMode
            playWhenReady = isPlaying()
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

                val actionToken = "get-current-position"
                fun getCurrentPosition() {
                    onPositionChanged(player.currentPosition)
                    if (player.isPlaying.not()) return
                    handler.postDelayed(POSITION_UPDATE_INTERVAL_MS, actionToken) { getCurrentPosition() }
                }
                val playerListener = object : Player.Listener {
                    val activity = context.findActivity()

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying.not()) {
                            handler.removeCallbacksAndMessages(actionToken)
                            activity?.setKeepScreenOn(false)
                            return
                        }
                        activity?.setKeepScreenOn(true)
                        handler.postDelayed(POSITION_UPDATE_INTERVAL_MS, actionToken) { getCurrentPosition() }
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
                    it.player?.playWhenReady = isPlaying()
                    it.onResume()
                }
                else -> Unit
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}
