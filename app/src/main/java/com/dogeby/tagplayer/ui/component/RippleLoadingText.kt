package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor

@Composable
fun RippleLoadingText(
    modifier: Modifier = Modifier,
    color: Color = RippleLoadingColor,
    shape: Shape = MaterialTheme.shapes.small,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    Text(
        text = "",
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color.copy(alpha = rippleAlpha))
    )
}
