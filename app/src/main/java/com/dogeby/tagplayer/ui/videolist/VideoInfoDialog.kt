package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoInfoDialog(
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
            Column {
                VideoInfoItem(
                    title = stringResource(id = R.string.name),
                    content = videoItem.name,
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.extension),
                    content = videoItem.extension,
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.duration),
                    content = videoItem.duration,
                )
                VideoInfoItem(
                    title = stringResource(id = R.string.size),
                    content = videoItem.formattedSize,
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
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_list_item_horizontal_padding)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoInfoDialogPreview() {
    TagPlayerTheme {
        VideoInfoDialog(
            videoItem = VideoItem(
                0, "", "동영상1", "MP4", "12:20", "10.23 MB", 1023000000, "/movie/", listOf("movie"),
                List(1) { Tag(name = "tag$it") },
            ),
            onDismissRequest = {},
            onConfirmButtonClick = {},
        )
    }
}
