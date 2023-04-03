package com.dogeby.tagplayer.ui.videolist

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.component.TagPlayerNavigationDrawer
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.dogeby.tagplayer.ui.theme.EmphasizedDecelerateEasing
import com.dogeby.tagplayer.ui.theme.MediumDuration4
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoListRoute(
    tagPlayerDrawerItems: List<TagPlayerDrawerItem>,
    onNavigateToRoute: (TagPlayerDrawerItem) -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToFilterSetting: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    onNavigateToVideoSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel(),
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    val videoListUiState: VideoListUiState by viewModel.videoListUiState.collectAsState()
    val isVideoFiltered: Boolean by viewModel.isVideoFiltered.collectAsState()
    val isSelectMode: Boolean by viewModel.isSelectMode.collectAsState()
    val isSelectedVideoItems: Map<Long, Boolean> = viewModel.isSelectedVideoItems
    val videoInfoDialogUiState: VideoInfoDialogUiState by viewModel.videoInfoDialogUiState.collectAsState()
    val videoListSortTypeUiState: VideoListSortTypeUiState by viewModel.videoListSortTypeUiState.collectAsState()

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) viewModel.updateVideoList()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    TagPlayerNavigationDrawer(
        startRoute = com.dogeby.tagplayer.ui.navigation.VideoListRoute,
        tagPlayerDrawerItems = tagPlayerDrawerItems,
        onItemClick = { onNavigateToRoute(it) },
        drawerState = drawerState,
    ) {
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
            onMenuButtonClick = { scope.launch { drawerState.open() } },
            isVideoFiltered = isVideoFiltered,
            modifier = modifier.fillMaxWidth(),
            onNavigateToFilterSetting = onNavigateToFilterSetting,
            setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
            updateVideo = viewModel::updateVideoList
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoListScreen(
    videoListUiState: VideoListUiState,
    isSelectMode: Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    videoInfoDialogUiState: VideoInfoDialogUiState,
    videoListSortTypeUiState: VideoListSortTypeUiState,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: () -> Unit,
    onNavigateToVideoSearch: () -> Unit,
    onToggleVideoItem: (Long) -> Unit,
    onSelectAllVideoItem: () -> Unit,
    onClearSelectedVideoItems: () -> Unit,
    onShowVideoInfoDialog: () -> Unit,
    onHideVideoInfoDialog: () -> Unit,
    onSortTypeSet: (VideoListSortType) -> Unit,
    onMenuButtonClick: () -> Unit,
    isVideoFiltered: Boolean,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null
) {
    var progressIndicatorState by rememberSaveable { mutableStateOf(videoListUiState is VideoListUiState.Loading) }
    var isShowBottomAppBarIconAnimation by remember { mutableStateOf(false) }
    var bottomBarShown by rememberSaveable { mutableStateOf(true) }.apply {
        if (isSelectMode) this.value = isSelectMode
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && setTopResumedActivityChangedListener != null) {

            setTopResumedActivityChangedListener { isTopResumedActivity: Boolean ->
                if (isTopResumedActivity && updateVideo != null) updateVideo()
            }
            return@DisposableEffect onDispose {
                setTopResumedActivityChangedListener(null)
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && updateVideo != null) updateVideo()
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
            topBar = {
                VideoListTopAppBar(
                    isSelectMode = isSelectMode,
                    onMenuButtonClick = onMenuButtonClick,
                    onClearButtonClick = onClearSelectedVideoItems,
                )
            },
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
                        isFilterButtonChecked = isVideoFiltered,
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
                        videoItems = videoListUiState.videoItems,
                        isSelectMode = isSelectMode,
                        isSelectedVideoItems = isSelectedVideoItems,
                        onNavigateToPlayer = onNavigateToPlayer,
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
    isSelectMode: Boolean,
    onMenuButtonClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(id = R.string.videoList_topAppBar_title)) },
        navigationIcon = {
            if (isSelectMode) {
                VideoListTopAppBarIconButtonAnimation(visible = true) {
                    IconButton(onClick = onClearButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                        )
                    }
                }
                return@TopAppBar
            }
            VideoListTopAppBarIconButtonAnimation(visible = true) {
                IconButton(onClick = onMenuButtonClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
private fun VideoListTopAppBarIconButtonAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(visible.not()).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = transitionState,
        modifier = modifier,
        enter = fadeIn(
            tween(
                durationMillis = MediumDuration4,
                easing = EmphasizedDecelerateEasing,
            )
        ),
        content = content,
    )
}
