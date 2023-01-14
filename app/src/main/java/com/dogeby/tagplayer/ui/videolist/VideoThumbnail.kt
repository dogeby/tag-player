package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dogeby.tagplayer.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VideoThumbnail(
    uri: String,
    width: Int,
    height: Int,
    duration: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Box {
            GlideImage(
                model = uri,
                contentDescription = null,
                modifier = modifier,
                contentScale = ContentScale.Crop,
            ) {
                it
                    .override(width, height)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .fallback(R.drawable.ic_broken_image)
            }

            val thumbnailDurationPadding =
                dimensionResource(id = R.dimen.videolist_video_thumbnail_duration_padding)
            VideoDuration(
                duration = duration,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = thumbnailDurationPadding,
                        bottom = thumbnailDurationPadding,
                    ),
            )
        }
    }
}

@Composable
fun VideoDuration(
    duration: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color.Black,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Text(
            text = duration,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_duration_horizontal_padding)),
        )
    }
}
