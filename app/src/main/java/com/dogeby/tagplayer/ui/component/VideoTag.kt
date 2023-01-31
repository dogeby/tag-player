package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import com.dogeby.tagplayer.R

@Composable
fun VideoTag(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_horizontal_padding)),
            maxLines = 1,
            style = textStyle,
        )
    }
}
