package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoTag(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    isEllipsis: Boolean = false
) {
    Surface(
        modifier = modifier.widthIn(dimensionResource(id = R.dimen.videoTag_min_width)),
        shape = shape,
        color = color,
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_horizontal_padding)),
            textAlign = TextAlign.Center,
            overflow = if (isEllipsis) TextOverflow.Ellipsis else TextOverflow.Clip,
            maxLines = 1,
            style = textStyle,
        )
    }
}

@Composable
fun RippleLoadingVideoTag(
    modifier: Modifier = Modifier,
    color: Color = RippleLoadingColor,
    shape: Shape = MaterialTheme.shapes.small,
    alpha: Float = rememberRippleLoadingEffectAlpha(),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Box(
        modifier = modifier
            .width(dimensionResource(id = R.dimen.videoTag_min_width))
            .clip(shape)
            .background(color.copy(alpha = alpha))
    ) {
        Text(
            text = "",
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_horizontal_padding)),
            style = textStyle,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoTagRippleLoadingPreview() {
    TagPlayerTheme {
        RippleLoadingVideoTag()
    }
}
