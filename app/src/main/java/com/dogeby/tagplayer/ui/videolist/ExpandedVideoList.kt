package com.dogeby.tagplayer.ui.videolist

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.RippleLoadingText
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoTag
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoThumbnail
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.component.extensions.OnFirstVisibleItemIndexChange
import com.dogeby.tagplayer.ui.component.rememberRippleLoadingEffectAlpha
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedVideoList(
    videoItems: List<VideoItem>,
    isSelectMode: () -> Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    firstVisibleItemIndex: Int = 0,
    lazyStaggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(firstVisibleItemIndex),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalItemSpacing: Dp = dimensionResource(id = R.dimen.padding_small),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    onToggleVideoSelection: (VideoItem) -> Unit = {},
    onScrollToEnd: (Boolean) -> Unit = {},
    onSaveVideoListInitialItemIndex: (Int) -> Unit = {},
) {
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

    lazyStaggeredGridState.OnReachedEnd(onScrollToEnd)
    lazyStaggeredGridState.OnFirstVisibleItemIndexChange {
        onSaveVideoListInitialItemIndex(it)
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dimensionResource(id = R.dimen.videolist_expanded_video_item_width)),
        modifier = modifier.fillMaxSize(),
        state = lazyStaggeredGridState,
        contentPadding = contentPadding,
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement,
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
fun ExpandedVideoCard(
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
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onClick(videoItem) },
                onLongClick = { onLongClick(videoItem) },
            ),
    ) {
        VideoListVideoThumbnail(
            uri = videoItem.uri,
            duration = videoItem.duration.toString()
        )
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
        ) {
            Text(text = videoItem.name, maxLines = 2, overflow = TextOverflow.Ellipsis)

            val tagListItemEndPadding = Modifier.padding(end = dimensionResource(id = R.dimen.videolist_video_tag_list_item_end_padding))
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(videoItem.parentDirectories) {
                    VideoTag(
                        name = it,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = tagListItemEndPadding,
                    )
                }
            }
            LazyRow(modifier = Modifier.padding(top = 4.dp)) {
                items(videoItem.tags) {
                    VideoTag(
                        name = it.name,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = tagListItemEndPadding,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedRippleLoadingVideoList(
    itemCount: Int,
    modifier: Modifier = Modifier,
    verticalItemSpacing: Dp = dimensionResource(id = R.dimen.padding_small),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dimensionResource(id = R.dimen.videolist_expanded_video_item_width)),
        modifier = modifier,
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement,
        userScrollEnabled = false,
    ) {
        items(itemCount) {
            ExpandedRippleLoadingVideoCard(
                modifier = Modifier.padding(videoItemContentPadding),
                containerRippleColor = containerRippleColor,
                contentRippleColor = contentRippleColor,
                rippleAlpha = rippleAlpha,
            )
        }
    }
}

@Composable
fun ExpandedRippleLoadingVideoCard(
    modifier: Modifier = Modifier,
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    Card(
        modifier = modifier
            .aspectRatio(16 / (LocalConfiguration.current.fontScale * 10 + 6f))
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = containerRippleColor.copy(alpha = rippleAlpha)
        ),
    ) {
        RippleLoadingVideoThumbnail(
            modifier = Modifier.aspectRatio(16 / 9f),
            shape = MaterialTheme.shapes.small,
            alpha = rippleAlpha,
        )
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
                Modifier.padding(end = dimensionResource(id = R.dimen.videolist_video_tag_list_item_end_padding))
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

@Preview(showBackground = true)
@Composable
private fun ExpandedRippleLoadingVideoCardPreview() {
    TagPlayerTheme {
        ExpandedRippleLoadingVideoCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedRippleLoadingVideoListPreview() {
    TagPlayerTheme {
        ExpandedRippleLoadingVideoList(3)
    }
}
