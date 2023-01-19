package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimation
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimationIconButton
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
    val isTagFiltered: Boolean by viewModel.isTagFiltered.collectAsState()
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
        isTagFiltered = isTagFiltered,
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
    isTagFiltered: Boolean,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var progressIndicatorState by rememberSaveable { mutableStateOf(videoListUiState is VideoListUiState.Loading) }
    var isShowBottomAppBarIconAnimation by remember { mutableStateOf(false) }
    var bottomBarShown by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        topBar = { VideoListTopAppBar() },
        bottomBar = {
            if (isSelectMode) {
                VideoItemBottomAppBar(
                    shown = bottomBarShown,
                    onAllItemSelectButtonClick = { /*TODO*/ },
                    onTagSettingButtonClick = { /*TODO*/ },
                    onInfoButtonClick = { /*TODO*/ },
                    isShowActionIconAnimation = isShowBottomAppBarIconAnimation
                )
            } else {
                VideoListBottomAppBar(
                    shown = bottomBarShown,
                    isFilterButtonChecked = isTagFiltered,
                    onSearchButtonClick = { /*TODO*/ },
                    onFilterButtonClick = onNavigateToFilterSetting,
                    onSortButtonClick = { /*TODO*/ },
                    isShowActionIconAnimation = isShowBottomAppBarIconAnimation
                )
            }
            isShowBottomAppBarIconAnimation = true
        },
    ) { contentPadding ->
        if (progressIndicatorState) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        }

        when (videoListUiState) {
            VideoListUiState.Loading -> {
                progressIndicatorState = true
            }
            is VideoListUiState.Success -> {
                progressIndicatorState = false
                VideoList(
                    modifier = Modifier.padding(contentPadding),
                    videoListUiState = videoListUiState,
                    isSelectMode = isSelectMode,
                    isSelectedVideoItems = isSelectedVideoItems,
                    onNavigateToPlayer = onNavigateToPlayer,
                    onToggleVideoItem = onToggleVideoItem,
                    onScrollingUp = { bottomBarShown = isSelectMode || it },
                )
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
fun VideoListBottomAppBar(
    shown: Boolean,
    isFilterButtonChecked: Boolean,
    onSearchButtonClick: () -> Unit,
    onFilterButtonClick: () -> Unit,
    onSortButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isShowActionIconAnimation: Boolean = true,
) {
    BottomAppBarAnimation(
        shown = shown,
    ) {
        BottomAppBar(
            modifier = modifier,
        ) {
            BottomAppBarAnimationIconButton(
                iconResId = R.drawable.ic_search,
                onClick = onSearchButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 400f,
            )
            BottomAppBarAnimationIconButton(
                iconResId = if (isFilterButtonChecked) R.drawable.ic_filled_filter else R.drawable.ic_outlined_filter,
                onClick = onFilterButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 200f,
            )
            BottomAppBarAnimationIconButton(
                iconResId = R.drawable.ic_sort,
                onClick = onSortButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 120f,
            )
        }
    }
}

@Composable
fun VideoItemBottomAppBar(
    shown: Boolean,
    onAllItemSelectButtonClick: () -> Unit,
    onTagSettingButtonClick: () -> Unit,
    onInfoButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isShowActionIconAnimation: Boolean = true,
) {
    BottomAppBarAnimation(
        shown = shown,
    ) {
        BottomAppBar(
            modifier = modifier,
        ) {
            BottomAppBarAnimationIconButton(
                iconResId = R.drawable.ic_all_select,
                onClick = onAllItemSelectButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 400f,
            )
            BottomAppBarAnimationIconButton(
                iconResId = R.drawable.ic_tag,
                onClick = onTagSettingButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 200f,
            )
            BottomAppBarAnimationIconButton(
                iconResId = R.drawable.ic_info,
                onClick = onInfoButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                stiffness = 120f,
            )
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
    onScrollingUp: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val videoListState = rememberLazyListState()
    onScrollingUp(videoListState.isScrollingUp())

    LazyColumn(
        modifier = modifier,
        state = videoListState,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
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

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Preview
@Composable
fun VideoListBottomAppBarPreview() {
    TagPlayerTheme {
        VideoListBottomAppBar(
            shown = true,
            isFilterButtonChecked = true,
            onSearchButtonClick = { },
            onFilterButtonClick = { },
            onSortButtonClick = { },
            isShowActionIconAnimation = true,
        )
    }
}
