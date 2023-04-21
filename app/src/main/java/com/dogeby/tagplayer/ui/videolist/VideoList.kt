package com.dogeby.tagplayer.ui.videolist

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.RippleLoadingText
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoTag
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoThumbnail
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.component.VideoThumbnail
import com.dogeby.tagplayer.ui.component.rememberRippleLoadingEffectAlpha
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun CompactVideoList(
    videoItems: List<VideoItem>,
    isSelectMode: () -> Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    onToggleVideoSelection: (VideoItem) -> Unit = {},
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null,
) {
    VideoListUpdate(setTopResumedActivityChangedListener, updateVideo)

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        header()
        if (videoItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight(0.5f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.videoList_listEmpty),
                    )
                }
            }
        }
        items(videoItems) { item ->
            CompactVideoCard(
                videoItem = item,
                isSelected = isSelectedVideoItems.getOrDefault(item.id, false),
                onClick = { videoItem ->
                    if (isSelectMode()) {
                        onToggleVideoSelection(videoItem)
                        return@CompactVideoCard
                    }
                    onNavigateToPlayer(videoItems.map { it.id }, videoItem.id)
                },
                onLongClick = onToggleVideoSelection,
                modifier = Modifier.padding(videoItemContentPadding),
            )
        }
        footer()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactVideoCard(
    videoItem: VideoItem,
    isSelected: Boolean,
    onClick: (VideoItem) -> Unit,
    onLongClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.videolist_compact_video_item_height))
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onClick(videoItem) },
                onLongClick = { onLongClick(videoItem) },
            ),
    ) {
        Row {
            VideoListVideoThumbnail(
                uri = videoItem.uri,
                duration = videoItem.duration.toString()
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
            ) {
                Text(text = videoItem.name, maxLines = 2, overflow = TextOverflow.Ellipsis)

                val tagListItemHorizontalPadding = Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_list_item_horizontal_padding))
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    LazyRow {
                        items(videoItem.parentDirectories) {
                            VideoTag(
                                name = it,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = tagListItemHorizontalPadding,
                            )
                        }
                    }
                    if (videoItem.tags.isNotEmpty()) {
                        LazyRow(modifier = Modifier.padding(top = 4.dp)) {
                            items(videoItem.tags) {
                                VideoTag(
                                    name = it.name,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = tagListItemHorizontalPadding,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactRippleLoadingVideoList(
    count: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        userScrollEnabled = false,
    ) {
        header()
        items(count) {
            CompactRippleLoadingVideoCard(
                modifier = Modifier.padding(videoItemContentPadding),
                containerRippleColor = containerRippleColor,
                contentRippleColor = contentRippleColor,
                rippleAlpha = rippleAlpha,
            )
        }
        footer()
    }
}

@Composable
private fun CompactRippleLoadingVideoCard(
    modifier: Modifier = Modifier,
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.videolist_compact_video_item_height))
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = containerRippleColor.copy(alpha = rippleAlpha)
        ),
    ) {
        Row {
            RippleLoadingVideoThumbnail(
                modifier = modifier.aspectRatio(16 / 9f),
                shape = MaterialTheme.shapes.small,
                alpha = rippleAlpha,
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
                ) {
                    RippleLoadingText(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = contentRippleColor,
                        rippleAlpha = rippleAlpha,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    val tagListItemModifier =
                        Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_list_item_horizontal_padding))
                    Row {
                        repeat(2) {
                            RippleLoadingVideoTag(
                                modifier = tagListItemModifier,
                                color = contentRippleColor,
                                alpha = rippleAlpha,
                            )
                        }
                    }
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        RippleLoadingVideoTag(
                            modifier = tagListItemModifier,
                            color = contentRippleColor,
                            alpha = rippleAlpha,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoListUpdate(
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && setTopResumedActivityChangedListener != null) {

            setTopResumedActivityChangedListener { isTopResumedActivity: Boolean ->
                if (isTopResumedActivity && updateVideo != null) updateVideo()
            }
            return@DisposableEffect onDispose {
                setTopResumedActivityChangedListener(null)
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && updateVideo != null) updateVideo()
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun VideoListVideoThumbnail(
    uri: String,
    duration: String,
    modifier: Modifier = Modifier,
) {
    VideoThumbnail(
        uri = uri,
        modifier = modifier.aspectRatio(16 / 9f),
        imageShape = MaterialTheme.shapes.small,
        applyContentBasedColorToBackgroundColor = true,
        contentScale = ContentScale.FillHeight,
        duration = duration,
        durationShape = MaterialTheme.shapes.small,
        frameTimeMicrosecond = integerResource(id = R.integer.videolist_video_thumbnail_frameTimeMicrosecond).toLong(),
    )
}

@Preview(showBackground = true)
@Composable
private fun CompactRippleLoadingVideoCardPreview() {
    TagPlayerTheme {
        CompactRippleLoadingVideoCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactRippleLoadingVideoListPreview() {
    TagPlayerTheme {
        CompactRippleLoadingVideoList(3)
    }
}
