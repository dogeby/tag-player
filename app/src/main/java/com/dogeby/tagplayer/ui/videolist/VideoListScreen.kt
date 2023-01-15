package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.permission.AppPermissionDeniedByExternalAction
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoListRoute(
    onExit: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel(),
) {
    val videoListUiState: VideoListUiState by viewModel.videoListUiState.collectAsState()
    val filteredTagsUiState: FilteredTagsUiState by viewModel.filteredTagsUiState.collectAsState()

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.updateVideoList()
        }
    }
    AppPermissionDeniedByExternalAction(onExit)

    VideoListScreen(
        videoListUiState = videoListUiState,
        filteredTagsUiState = filteredTagsUiState,
        onNavigateToPlayer = onNavigateToPlayer,
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
            VideoListItem(
                videoItem = videoItem,
                onNavigateToPlayer = onNavigateToPlayer,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            )
        }
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
