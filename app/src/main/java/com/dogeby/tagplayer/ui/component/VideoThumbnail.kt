package com.dogeby.tagplayer.ui.component

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.palette.graphics.Palette
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
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
    applyContentBasedColorToBackgroundColor: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    imageContentDescription: String? = null,
    duration: String? = null,
    durationShape: Shape = RectangleShape,
    @IntRange(from = 0) frameTimeMicrosecond: Long = 0,
) {
    var thumbnailBackgroundColor by remember {
        mutableStateOf(backgroundColor)
    }
    Surface(
        shape = imageShape,
        color = thumbnailBackgroundColor,
        modifier = modifier,
    ) {
        Box {
            GlideImage(
                model = uri,
                contentDescription = imageContentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            ) { requestBuilder ->
                requestBuilder
                    .override(width, height)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .fallback(R.drawable.ic_broken_image)
                    .frame(frameTimeMicrosecond)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            if (resource is BitmapDrawable && applyContentBasedColorToBackgroundColor) {
                                val palette = Palette.from(resource.bitmap).generate()

                                palette.lightMutedSwatch?.let { swatch ->
                                    thumbnailBackgroundColor = Color(swatch.rgb)
                                    return false
                                }
                                palette.dominantSwatch?.let { swatch ->
                                    thumbnailBackgroundColor = Color(swatch.rgb)
                                    return false
                                }
                                if (palette.swatches.isEmpty()) return false
                                thumbnailBackgroundColor = Color(palette.swatches.first().rgb)
                            }
                            return false
                        }
                    })
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
    modifier: Modifier = Modifier,
    width: Dp = dimensionResource(id = R.dimen.videoSmallThumbnail_width),
    height: Dp = dimensionResource(id = R.dimen.videoSmallThumbnail_height),
    imageShape: Shape = RectangleShape,
    backgroundColor: Color = VideoThumbnailBackgroundColor,
    applyContentBasedColorToBackgroundColor: Boolean = false,
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
        applyContentBasedColorToBackgroundColor = applyContentBasedColorToBackgroundColor,
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

@Composable
fun RippleLoadingVideoThumbnail(
    modifier: Modifier = Modifier,
    color: Color = RippleLoadingColor,
    shape: Shape = RectangleShape,
    alpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    Box(
        modifier = modifier
            .sizeIn(
                minWidth = dimensionResource(id = R.dimen.videoSmallThumbnail_width),
                minHeight = dimensionResource(id = R.dimen.videoSmallThumbnail_height)
            )
            .clip(shape)
            .background(color.copy(alpha = alpha))
    )
}

@Preview(showBackground = true)
@Composable
fun RippleLoadingVideoThumbnailPreview() {
    TagPlayerTheme {
        RippleLoadingVideoThumbnail(shape = MaterialTheme.shapes.small)
    }
}
