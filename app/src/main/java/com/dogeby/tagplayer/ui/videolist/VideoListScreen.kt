package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.permission.AppPermissionDeniedByExternalAction
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
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
    val isFilteredTag: Boolean by viewModel.isFilteredTag.collectAsState()
    val isSelectMode: Boolean by viewModel.isSelectMode.collectAsState()
    val isSelectedVideoItems: Map<Long, Boolean> = viewModel.isSelectedVideoItems

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.updateVideoList()
        }
    }
    AppPermissionDeniedByExternalAction(onExit)

    VideoListScreen(
        videoListUiState = videoListUiState,
        isSelectMode = isSelectMode,
        isSelectedVideoItems = isSelectedVideoItems.toMap(),
        onNavigateToPlayer = onNavigateToPlayer,
        onToggleVideoItem = { id -> viewModel.toggleIsSelectedVideoItems(id) },
        isFilteredTag = isFilteredTag,
        modifier = modifier.fillMaxWidth(),
        onNavigateToFilterSetting = onNavigateToFilterSetting,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    videoListUiState: VideoListUiState,
    isSelectMode: Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: () -> Unit,
    onToggleVideoItem: (Long) -> Unit,
    isFilteredTag: Boolean,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var progressIndicatorState by rememberSaveable { mutableStateOf(videoListUiState is VideoListUiState.Loading) }
    if (progressIndicatorState) LinearProgressIndicator(modifier = modifier)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            VideoListBottomAppBar(
                isFilterButtonChecked = isFilteredTag,
                onSearchButtonClick = { /*TODO*/ },
                onFilterButtonClick = onNavigateToFilterSetting,
                onSortButtonClick = { /*TODO*/ },
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
        ) {
            when (videoListUiState) {
                VideoListUiState.Loading -> {
                    progressIndicatorState = true
                }
                is VideoListUiState.Success -> {
                    progressIndicatorState = false
                    VideoList(
                        videoListUiState = videoListUiState,
                        isSelectMode = isSelectMode,
                        isSelectedVideoItems = isSelectedVideoItems,
                        onNavigateToPlayer = onNavigateToPlayer,
                        onToggleVideoItem = onToggleVideoItem,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
            }
        }
    }
}

@Composable
fun VideoList(
    videoListUiState: VideoListUiState.Success,
    isSelectedVideoItems: Map<Long, Boolean>,
    isSelectMode: Boolean,
    onNavigateToPlayer: () -> Unit,
    onToggleVideoItem: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    ) {
        items(videoListUiState.videoItems) { videoItem ->
            VideoListItem(
                videoItem = videoItem,
                isSelected = isSelectedVideoItems.getOrDefault(videoItem.id, false),
                onClick = {
                    if (isSelectMode) {
                        onToggleVideoItem(videoItem.id)
                    } else {
                        onNavigateToPlayer()
                    }
                },
                onLongClick = { onToggleVideoItem(videoItem.id) },
            )
        }
    }
}
