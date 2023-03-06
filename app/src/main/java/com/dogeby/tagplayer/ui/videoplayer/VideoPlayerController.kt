package com.dogeby.tagplayer.ui.videoplayer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarIndicatorColor
import com.dogeby.tagplayer.ui.theme.PlayerProgressBarTrackColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoPlayerProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier,
        color = PlayerProgressBarIndicatorColor,
        trackColor = PlayerProgressBarTrackColor,
    )
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerProgressBarPreview() {
    TagPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Box {
                VideoPlayerProgressBar(
                    progress = 0.5f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
