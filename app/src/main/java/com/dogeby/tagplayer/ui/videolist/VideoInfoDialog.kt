package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.video.VideoDuration
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.component.formatToSimpleSize
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoInfoDialog(
    videoItems: List<VideoItem>,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (videoItems.count()) {
        0 -> { onDismissRequest() }
        1 -> {
            SingleVideoInfoDialog(
                videoItem = videoItems.first(),
                onDismissRequest = onDismissRequest,
                onConfirmButtonClick = onConfirmButtonClick,
                modifier = modifier,
            )
        }
        else -> {
            MultiVideoInfoDialog(
                representativeName = videoItems.first().name,
                count = videoItems.count(),
                totalSize = videoItems.fold(0L) { acc: Long, videoItem: VideoItem -> acc + videoItem.size }.formatToSimpleSize(),
                onDismissRequest = onDismissRequest,
                onConfirmButtonClick = onConfirmButtonClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun SingleVideoInfoDialog(
    videoItem: VideoItem,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.videoInfoDialog_title))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                VideoInfoItem(
                    title = stringResource(id = R.string.title),
                    content = videoItem.name,
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.extension),
                    content = videoItem.extension,
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.duration),
                    content = videoItem.duration.toString(),
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.size),
                    content = videoItem.size.formatToSimpleSize(),
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.path),
                    content = videoItem.path,
                )
                if (videoItem.tags.isNotEmpty()) {
                    VideoInfoTags(title = stringResource(id = R.string.tag), tags = videoItem.tags)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun VideoInfoItem(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small)),
    ) {
        Text(text = title)
        Spacer(Modifier.height(dimensionResource(id = R.dimen.videoInfoDialog_infoItemTitleContentPadding)))
        Text(
            text = content,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun VideoInfoTags(
    title: String,
    tags: List<Tag>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = title)
        Spacer(Modifier.height(dimensionResource(id = R.dimen.videoInfoDialog_infoItemTitleContentPadding)))
        LazyRow {
            items(tags) {
                VideoTag(
                    name = it.name,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(end = dimensionResource(id = R.dimen.videolist_video_tag_list_item_end_padding)),
                )
            }
        }
    }
}

@Composable
fun MultiVideoInfoDialog(
    representativeName: String,
    count: Int,
    totalSize: String,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.videoInfoDialog_title))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                VideoInfoItem(
                    title = stringResource(id = R.string.title),
                    content = stringResource(
                        R.string.multiVideoInfoDialog_name,
                        representativeName,
                        count - 1,
                    ),
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.total_size),
                    content = totalSize,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun SingleVideoInfoDialogPreview() {
    TagPlayerTheme {
        SingleVideoInfoDialog(
            videoItem = VideoItem(
                0, "", "동영상1", "MP4", VideoDuration(100), 1023000000, "/movie/", listOf("movie"),
                List(1) { Tag(name = "tag$it") },
            ),
            onDismissRequest = {},
            onConfirmButtonClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultiVideoInfoDialogPreview() {
    TagPlayerTheme {
        MultiVideoInfoDialog(
            representativeName = "동영상1",
            count = 3,
            totalSize = "10.23 MB",
            onDismissRequest = {},
            onConfirmButtonClick = {},
        )
    }
}
