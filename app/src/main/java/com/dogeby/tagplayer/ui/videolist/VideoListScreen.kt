package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoListRoute(
    onNavigateToPlayer: () -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel(),
) {
    val videoListUiState: VideoListUiState by viewModel.videoListUiState.collectAsState()
    val filteredTagsUiState: FilteredTagsUiState by viewModel.filteredTagsUiState.collectAsState()

    VideoListScreen(
        videoListUiState = videoListUiState,
        filteredTagsUiState = filteredTagsUiState,
        onNavigateToPlayer,
        modifier = modifier.fillMaxWidth(),
        onNavigateToFilterSetting = onNavigateToFilterSetting,
    )
}

@Composable
fun VideoListScreen(
    videoListUiState: VideoListUiState,
    filteredTagsUiState: FilteredTagsUiState,
    onNavigateToPlayer: () -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var progressIndicatorState by rememberSaveable { mutableStateOf(videoListUiState is VideoListUiState.Loading) }
    if (progressIndicatorState) LinearProgressIndicator(modifier = modifier)

    Column(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
    ) {
        VideoListSetting(
            filteredTagsUiState = filteredTagsUiState,
            onNavigateToFilterSetting = onNavigateToFilterSetting,
        )

        when (videoListUiState) {
            VideoListUiState.Loading -> {
                progressIndicatorState = true
            }
            is VideoListUiState.Success -> {
                progressIndicatorState = false
                VideoList(
                    videoListUiState = videoListUiState,
                    onNavigateToPlayer = onNavigateToPlayer,
                )
            }
        }
    }
}

@Composable
fun VideoListSetting(
    filteredTagsUiState: FilteredTagsUiState,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTags =
        if (filteredTagsUiState is FilteredTagsUiState.Success && filteredTagsUiState.filteredTagNames.isNotEmpty()) {
            filteredTagsUiState.filteredTagNames.joinToString(prefix = ": ")
        } else {
            ""
        }
    Row(modifier = modifier) {
        SettingAssistChip(
            text = "${stringResource(id = R.string.videolist_filter)}$filteredTags",
            onClick = onNavigateToFilterSetting,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_list),
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
fun VideoList(
    videoListUiState: VideoListUiState.Success,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(videoListUiState.videoItems) { videoItem ->
            VideoItem(
                videoItem = videoItem,
                onNavigateToPlayer = onNavigateToPlayer,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAssistChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = modifier.widthIn(max = dimensionResource(id = R.dimen.videolist_setting_assist_chip_max_width)),
        leadingIcon = leadingIcon,
    )
}

@Composable
fun VideoItem(
    videoItem: VideoItem,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.videolist_video_item_height))
            .clickable { onNavigateToPlayer() },
    ) {
        VideoThumbnail(
            uri = videoItem.uri,
            width = integerResource(id = R.integer.videolist_video_thumbnail_width),
            height = integerResource(id = R.integer.videolist_video_thumbnail_height),
            duration = videoItem.duration,
            modifier = Modifier.width(dimensionResource(id = R.dimen.videolist_video_thumbnail_width)),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(dimensionResource(id = R.dimen.videolist_video_item_info_padding)),
        ) {
            Text(text = videoItem.name, maxLines = 2, overflow = TextOverflow.Ellipsis)

            val tagListItemHorizontalPadding = Modifier.padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_list_item_horizontal_padding))
            LazyRow(modifier = Modifier.align(Alignment.BottomStart)) {
                items(videoItem.parentDirectories) {
                    VideoTag(
                        name = it,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = tagListItemHorizontalPadding,
                    )
                }
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
                contentScale = ContentScale.Crop
            ) {
                it
                    .override(width, height)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .fallback(R.drawable.ic_broken_image)
            }

            val thumbnailDurationPadding = dimensionResource(id = R.dimen.videolist_video_thumbnail_duration_padding)
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

@Composable
fun VideoTag(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color,
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.videolist_video_tag_horizontal_padding)),
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview(showBackground = true, heightDp = 640, widthDp = 360)
@Composable
fun VideoListSettingPreview() {
    TagPlayerTheme {
        VideoListSetting(
            filteredTagsUiState = FilteredTagsUiState.Success(List(4) { "tag$it" }),
            onNavigateToFilterSetting = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingAssistChipPreview() {
    TagPlayerTheme {
        SettingAssistChip(
            text = "필터: Media",
            onClick = { },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_list),
                    contentDescription = null,
                )
            },
        )
    }
}
