package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import com.dogeby.tagplayer.ui.theme.VideoListThumbnailBackgroundColor
import com.dogeby.tagplayer.ui.videolist.CompactVideoList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagDetailCard(
    tagName: () -> String,
    videoItems: () -> List<VideoItem>,
    thumbnailSize: DpSize,
    onPlayButtonClick: (List<Long>, Long) -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    isSelectMode: () -> Boolean = { false },
    isSelectedVideoItems: Map<Long, Boolean> = emptyMap(),
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null,
    onToggleVideoSelection: (VideoItem) -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = shape,
    ) {
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null,
        ) {
            CompactVideoList(
                videoItems = videoItems(),
                isSelectMode = isSelectMode,
                isSelectedVideoItems = isSelectedVideoItems,
                onNavigateToPlayer = onPlayButtonClick,
                contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.padding_small)),
                videoItemContentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small)),
                header = {
                    if (videoItems().isNotEmpty()) {
                        item {
                            VideoThumbnail(
                                uri = videoItems().first().uri,
                                width = thumbnailSize.width,
                                height = thumbnailSize.height,
                                modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
                                imageShape = RoundedCornerShape(0, 0, 4, 4),
                                backgroundColor = VideoListThumbnailBackgroundColor,
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                    }
                    stickyHeader {
                        TagDetailCardHeader(
                            tagName = tagName,
                            videoItems = videoItems,
                            onPlayButtonClick = onPlayButtonClick,
                            onEditButtonClick = onEditButtonClick,
                            onDeleteButtonClick = onDeleteButtonClick,
                        )
                    }
                },
                setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                updateVideo = updateVideo,
                onToggleVideoSelection = onToggleVideoSelection,
            )
        }
    }
}

@Composable
private fun TagDetailCardHeader(
    tagName: () -> String,
    videoItems: () -> List<VideoItem>,
    onPlayButtonClick: (List<Long>, Long) -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            Text(
                text = tagName(),
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                minLines = 1,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
            ) {
                TagManageMenuMoreHorizButton(
                    onEditButtonClick = onEditButtonClick,
                    onDeleteButtonClick = onDeleteButtonClick,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val videoIds = videoItems().map { it.id }
                    onPlayButtonClick(videoIds, videoIds.first())
                },
                enabled = videoItems().isNotEmpty(),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_play),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedTagDetailCard(
    tagName: () -> String,
    videoItems: () -> List<VideoItem>,
    onPlayButtonClick: (List<Long>, Long) -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    isSelectMode: () -> Boolean = { false },
    isSelectedVideoItems: Map<Long, Boolean> = emptyMap(),
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null,
    onToggleVideoSelection: (VideoItem) -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = shape,
    ) {
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                val videoItemsValue = videoItems()
                CompactVideoList(
                    videoItems = videoItemsValue,
                    isSelectMode = isSelectMode,
                    isSelectedVideoItems = isSelectedVideoItems,
                    onNavigateToPlayer = onPlayButtonClick,
                    modifier = Modifier.widthIn(max = dimensionResource(id = R.dimen.tagdetailcard_expanded_width)),
                    contentPadding = PaddingValues(bottom = dimensionResource(id = R.dimen.padding_small)),
                    videoItemContentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    header = {
                        item {
                            if (videoItemsValue.isEmpty()) {
                                TagDetailCardHeader(
                                    tagName = tagName,
                                    videoItems = videoItems,
                                    onPlayButtonClick = onPlayButtonClick,
                                    onEditButtonClick = onEditButtonClick,
                                    onDeleteButtonClick = onDeleteButtonClick,
                                )
                                return@item
                            }
                            ExpandedTagDetailCardHeader(
                                tagName = tagName,
                                videoItems = videoItems,
                                onPlayButtonClick = onPlayButtonClick,
                                onEditButtonClick = onEditButtonClick,
                                onDeleteButtonClick = onDeleteButtonClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                            )
                        }
                    },
                    setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                    updateVideo = updateVideo,
                    onToggleVideoSelection = onToggleVideoSelection,
                )
            }
        }
    }
}

@Composable
private fun ExpandedTagDetailCardHeader(
    tagName: () -> String,
    videoItems: () -> List<VideoItem>,
    onPlayButtonClick: (List<Long>, Long) -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(Modifier.height(dimensionResource(id = R.dimen.tagdetailcard_expanded_header_thumbnail_width) / 16 * 9)) {
            if (videoItems().isNotEmpty()) {
                VideoThumbnail(
                    uri = videoItems().first().uri,
                    width = integerResource(id = R.integer.videolist_video_thumbnail_width),
                    height = integerResource(id = R.integer.videolist_video_thumbnail_height),
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.tagdetailcard_expanded_header_thumbnail_width))
                        .aspectRatio(16 / 9f),
                    imageShape = RoundedCornerShape(0, 0, 4, 4),
                    backgroundColor = VideoListThumbnailBackgroundColor,
                    contentScale = ContentScale.FillHeight,
                )
            }
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            ) {
                Text(
                    text = tagName(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    minLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                    ) {
                        TagManageMenuMoreHorizButton(
                            onEditButtonClick = onEditButtonClick,
                            onDeleteButtonClick = onDeleteButtonClick,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val videoIds = videoItems().map { it.id }
                            onPlayButtonClick(videoIds, videoIds.first())
                        },
                        enabled = videoItems().isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_play),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
fun TagDetailCardPreview() {
    TagPlayerTheme {
        TagDetailCard(
            tagName = { "Tag Name" },
            videoItems = { emptyList() },
            thumbnailSize = DpSize(165.dp, 100.dp),
            onPlayButtonClick = { _, _ -> },
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagDetailCardHeaderPreview() {
    TagPlayerTheme {
        TagDetailCardHeader(
            tagName = { "태그 이름" },
            videoItems = { emptyList() },
            onPlayButtonClick = { _, _ -> },
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
fun ExpandedTagDetailCardPreview() {
    TagPlayerTheme {
        ExpandedTagDetailCard(
            tagName = { "Tag Name" },
            videoItems = { emptyList() },
            onPlayButtonClick = { _, _ -> },
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandedTagDetailCardHeaderPreview() {
    TagPlayerTheme {
        ExpandedTagDetailCardHeader(
            tagName = { "태그 이름" },
            videoItems = { emptyList() },
            onPlayButtonClick = { _, _ -> },
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}
