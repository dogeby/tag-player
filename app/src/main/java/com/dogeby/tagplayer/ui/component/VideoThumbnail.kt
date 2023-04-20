package com.dogeby.tagplayer.ui.component

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.VideoThumbnailBackgroundColor

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VideoThumbnail(
    uri: String,
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    imageShape: Shape = RectangleShape,
    backgroundColor: Color = VideoThumbnailBackgroundColor,
    contentScale: ContentScale = ContentScale.Fit,
    imageContentDescription: String? = null,
    duration: String? = null,
    durationShape: Shape = RectangleShape,
    @IntRange(from = 0) frameTimeMicrosecond: Long = 0,
) {
    Surface(
        shape = imageShape,
        color = backgroundColor,
        modifier = modifier,
    ) {
        Box {
            GlideImage(
                model = uri,
                contentDescription = imageContentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            ) {
                it
                    .override(width, height)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .fallback(R.drawable.ic_broken_image)
                    .frame(frameTimeMicrosecond)
            }

            if (duration != null) {
                val thumbnailDurationPadding = dimensionResource(id = R.dimen.videolist_video_thumbnail_duration_padding)
                VideoDuration(
                    duration = duration,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = thumbnailDurationPadding,
                            bottom = thumbnailDurationPadding,
                        ),
                    shape = durationShape
                )
            }
        }
    }
}

@Composable
fun VideoThumbnail(
    uri: String,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    imageShape: Shape = RectangleShape,
    backgroundColor: Color = VideoThumbnailBackgroundColor,
    contentScale: ContentScale = ContentScale.Fit,
    imageContentDescription: String? = null,
    duration: String? = null,
    durationShape: Shape = RectangleShape,
    @IntRange(from = 0) frameTimeMicrosecond: Long = 0,
) {
    VideoThumbnail(
        uri = uri,
        width = width.toPx(),
        height = height.toPx(),
        modifier = modifier,
        imageShape = imageShape,
        backgroundColor = backgroundColor,
        contentScale = contentScale,
        imageContentDescription = imageContentDescription,
        duration = duration,
        durationShape = durationShape,
        frameTimeMicrosecond = frameTimeMicrosecond,
    )
}

@Composable
private fun Dp.toPx(): Int {
    return LocalDensity.current.run {
        this@toPx.toPx().toInt()
    }
}

@Composable
fun VideoDuration(
    duration: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
) {
    Surface(
        color = Color.Black,
        shape = shape,
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
