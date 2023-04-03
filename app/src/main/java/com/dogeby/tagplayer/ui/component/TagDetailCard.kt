package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import com.dogeby.tagplayer.ui.videolist.videoList

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
) {
    Card(
        modifier = modifier,
        shape = shape,
    ) {
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null,
        ) {
            LazyColumn {
                val videoItemsValue = videoItems()
                if (videoItemsValue.isNotEmpty()) {
                    item {
                        VideoThumbnail(
                            uri = videoItemsValue.first().uri,
                            width = thumbnailSize.width,
                            height = thumbnailSize.height,
                            modifier = Modifier.fillMaxHeight(0.3f),
                            imageShape = RoundedCornerShape(0, 0, 4, 4),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                stickyHeader {
                    Surface(
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
                                    val videoIds = videoItemsValue.map { it.id }
                                    onPlayButtonClick(videoIds, videoIds.first())
                                },
                                enabled = videoItemsValue.isNotEmpty(),
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                videoList(
                    videoItems = videoItemsValue,
                    isSelectedVideoItems = null,
                    isSelectMode = false,
                    onNavigateToPlayer = onPlayButtonClick,
                    onToggleVideoItem = {},
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
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
