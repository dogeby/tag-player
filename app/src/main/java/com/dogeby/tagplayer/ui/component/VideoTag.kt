package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.dogeby.tagplayer.R

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
