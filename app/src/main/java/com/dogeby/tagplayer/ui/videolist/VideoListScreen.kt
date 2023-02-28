package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoListRoute(
    onNavigateToPlayer: (List<Long>) -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    onNavigateToVideoSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel(),
) {
    val videoListUiState: VideoListUiState by viewModel.videoListUiState.collectAsState()
    val isTagFiltered: Boolean by viewModel.isTagFiltered.collectAsState()
    val isSelectMode: Boolean by viewModel.isSelectMode.collectAsState()
    val isSelectedVideoItems: Map<Long, Boolean> = viewModel.isSelectedVideoItems
    val videoInfoDialogUiState: VideoInfoDialogUiState by viewModel.videoInfoDialogUiState.collectAsState()
    val videoListSortTypeUiState: VideoListSortTypeUiState by viewModel.videoListSortTypeUiState.collectAsState()

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.updateVideoList()
        }
    }

    VideoListScreen(
        videoListUiState = videoListUiState,
        isSelectMode = isSelectMode,
        isSelectedVideoItems = isSelectedVideoItems.toMap(),
        videoInfoDialogUiState = videoInfoDialogUiState,
        videoListSortTypeUiState = videoListSortTypeUiState,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToTagSetting = { onNavigateToTagSetting(isSelectedVideoItems.filterValues { it }.keys.toList()) },
        onNavigateToVideoSearch = onNavigateToVideoSearch,
        onToggleVideoItem = { id -> viewModel.toggleIsSelectedVideoItems(id) },
        onSelectAllVideoItem = viewModel::selectAllVideoItems,
        onClearSelectedVideoItems = viewModel::clearIsSelectedVideoItems,
        onShowVideoInfoDialog = viewModel::showVideoInfoDialog,
        onHideVideoInfoDialog = viewModel::hideVideoInfoDialog,
        onSortTypeSet = viewModel::setSortType,
        isTagFiltered = isTagFiltered,
        modifier = modifier.fillMaxWidth(),
        onNavigateToFilterSetting = onNavigateToFilterSetting,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VideoListScreen(
    videoListUiState: VideoListUiState,
    isSelectMode: Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    videoInfoDialogUiState: VideoInfoDialogUiState,
    videoListSortTypeUiState: VideoListSortTypeUiState,
    onNavigateToPlayer: (List<Long>) -> Unit,
    onNavigateToTagSetting: () -> Unit,
    onNavigateToVideoSearch: () -> Unit,
    onToggleVideoItem: (Long) -> Unit,
    onSelectAllVideoItem: () -> Unit,
    onClearSelectedVideoItems: () -> Unit,
    onShowVideoInfoDialog: () -> Unit,
    onHideVideoInfoDialog: () -> Unit,
    onSortTypeSet: (VideoListSortType) -> Unit,
    isTagFiltered: Boolean,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var progressIndicatorState by rememberSaveable { mutableStateOf(videoListUiState is VideoListUiState.Loading) }
    var isShowBottomAppBarIconAnimation by remember { mutableStateOf(false) }
    var bottomBarShown by rememberSaveable { mutableStateOf(true) }.apply {
        if (isSelectMode) this.value = isSelectMode
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            bottomBarShown = isSelectMode || available.y > 0
            return super.onPreScroll(available, source)
        }
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(nestedScrollConnection),
            topBar = { VideoListTopAppBar() },
            bottomBar = {
                if (isSelectMode) {
                    VideoItemBottomAppBar(
                        shown = bottomBarShown,
                        onAllItemSelectButtonClick = onSelectAllVideoItem,
                        onTagSettingButtonClick = onNavigateToTagSetting,
                        onInfoButtonClick = onShowVideoInfoDialog,
                        onClearSelectedVideoItems = onClearSelectedVideoItems,
                        isShowActionIconAnimation = isShowBottomAppBarIconAnimation,
                    )
                } else {
                    VideoListBottomAppBar(
                        shown = bottomBarShown,
                        isFilterButtonChecked = isTagFiltered,
                        videoListSortTypeUiState = videoListSortTypeUiState,
                        onSearchButtonClick = onNavigateToVideoSearch,
                        onFilterButtonClick = onNavigateToFilterSetting,
                        onSortButtonClick = { },
                        onSortTypeSet = onSortTypeSet,
                        isShowActionIconAnimation = isShowBottomAppBarIconAnimation,
                    )
                }
                isShowBottomAppBarIconAnimation = true
            },
        ) { contentPadding ->
            if (progressIndicatorState) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                )
            }

            when (videoInfoDialogUiState) {
                VideoInfoDialogUiState.Hide -> {}
                is VideoInfoDialogUiState.ShowSingleInfo -> {
                    VideoInfoDialog(
                        videoItem = videoInfoDialogUiState.videoItem,
                        onDismissRequest = onHideVideoInfoDialog,
                        onConfirmButtonClick = onHideVideoInfoDialog,
                    )
                }
                is VideoInfoDialogUiState.ShowMultiInfo -> {
                    MultiVideoInfoDialog(
                        representativeName = videoInfoDialogUiState.representativeName,
                        count = videoInfoDialogUiState.count,
                        totalSize = videoInfoDialogUiState.totalSize,
                        onDismissRequest = onHideVideoInfoDialog,
                        onConfirmButtonClick = onHideVideoInfoDialog,
                    )
                }
            }

            when (videoListUiState) {
                VideoListUiState.Loading -> {
                    progressIndicatorState = true
                }
                VideoListUiState.Empty -> {
                    progressIndicatorState = false
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoList_listEmpty),
                        modifier = modifier.padding(contentPadding)
                    )
                }
                is VideoListUiState.Success -> {
                    progressIndicatorState = false
                    VideoList(
                        modifier = Modifier.padding(contentPadding),
                        videoListUiState = videoListUiState,
                        isSelectMode = isSelectMode,
                        isSelectedVideoItems = isSelectedVideoItems,
                        onNavigateToPlayer = { onNavigateToPlayer(videoListUiState.videoItems.map { it.id }) },
                        onToggleVideoItem = onToggleVideoItem,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListTopAppBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(id = R.string.videoList_topAppBar_title)) },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
fun VideoList(
    videoListUiState: VideoListUiState.Success,
    isSelectedVideoItems: Map<Long, Boolean>,
    isSelectMode: Boolean,
    onNavigateToPlayer: () -> Unit,
    onToggleVideoItem: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_small))
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
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
