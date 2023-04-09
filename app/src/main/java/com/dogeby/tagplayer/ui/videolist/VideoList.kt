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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.component.VideoThumbnail
import com.dogeby.tagplayer.ui.theme.VideoListThumbnailBackgroundColor

@Composable
private fun VideoListUpdate(
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
fun CompactVideoList(
    videoItems: List<VideoItem>,
    isSelectMode: () -> Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
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
            VideoThumbnail(
                uri = videoItem.uri,
                width = integerResource(id = R.integer.videolist_video_thumbnail_width),
                height = integerResource(id = R.integer.videolist_video_thumbnail_height),
                modifier = Modifier.aspectRatio(16 / 9f),
                imageShape = MaterialTheme.shapes.small,
                backgroundColor = VideoListThumbnailBackgroundColor,
                contentScale = ContentScale.Crop,
                duration = videoItem.duration.toString(),
                durationShape = MaterialTheme.shapes.small,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedVideoList(
    videoItems: List<VideoItem>,
    isSelectMode: () -> Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    onToggleVideoSelection: (VideoItem) -> Unit = {},
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null,
) {
    VideoListUpdate(setTopResumedActivityChangedListener, updateVideo)

    if (videoItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(id = R.string.videoList_listEmpty))
        }
        return
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dimensionResource(id = R.dimen.videolist_expanded_video_item_width)),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalItemSpacing = dimensionResource(id = R.dimen.padding_small),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    ) {
        items(videoItems) { item ->
            ExpandedVideoCard(
                videoItem = item,
                isSelected = isSelectedVideoItems.getOrDefault(item.id, false),
                onClick = { videoItem ->
                    if (isSelectMode()) {
                        onToggleVideoSelection(videoItem)
                        return@ExpandedVideoCard
                    }
                    onNavigateToPlayer(videoItems.map { it.id }, videoItem.id)
                },
                onLongClick = onToggleVideoSelection,
                modifier = Modifier.padding(videoItemContentPadding),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpandedVideoCard(
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
            .aspectRatio(16 / (LocalConfiguration.current.fontScale * 10 + 6f))
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onClick(videoItem) },
                onLongClick = { onLongClick(videoItem) },
            ),
    ) {
        VideoThumbnail(
            uri = videoItem.uri,
            width = integerResource(id = R.integer.videolist_video_thumbnail_width),
            height = integerResource(id = R.integer.videolist_video_thumbnail_height),
            modifier = Modifier.aspectRatio(16 / 9f),
            imageShape = MaterialTheme.shapes.small,
            backgroundColor = VideoListThumbnailBackgroundColor,
            contentScale = ContentScale.Crop,
            duration = videoItem.duration.toString(),
            durationShape = MaterialTheme.shapes.small,
        )

        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
        ) {
            Text(text = videoItem.name, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.weight(1f))

            val tagListItemHorizontalPadding = Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_list_item_horizontal_padding))
            LazyRow {
                items(videoItem.parentDirectories) {
                    VideoTag(
                        name = it,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = tagListItemHorizontalPadding,
                    )
                }
            }
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
