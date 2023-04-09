package com.dogeby.tagplayer.ui.videolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.component.TagPlayerNavigationDrawer
import com.dogeby.tagplayer.ui.component.WindowInfo
import com.dogeby.tagplayer.ui.component.rememberWindowInfo
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
            videoListSortTypeUiState = videoListSortTypeUiState,
            onNavigateToPlayer = onNavigateToPlayer,
            onNavigateToTagSetting = onNavigateToTagSetting,
            onNavigateToVideoSearch = onNavigateToVideoSearch,
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
    videoListSortTypeUiState: VideoListSortTypeUiState,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    onNavigateToVideoSearch: () -> Unit,
    onSortTypeSet: (VideoListSortType) -> Unit,
    onMenuButtonClick: () -> Unit,
    isVideoFiltered: Boolean,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null
) {
    var isSelectMode by remember(videoListUiState) {
        mutableStateOf(false)
    }
    val isSelectedVideoItems = remember(videoListUiState) {
        mutableStateMapOf<Long, Boolean>()
    }
    val toggleVideoSelection = remember(videoListUiState) {
        { videoItem: VideoItem ->
            isSelectedVideoItems.compute(videoItem.id) { _, v ->
                v?.not() ?: true
            }
            isSelectMode = isSelectedVideoItems.all { it.value.not() }.not()
        }
    }

    var isShowVideoInfoDialog by remember {
        mutableStateOf(false)
    }

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
            topBar = {
                VideoListTopAppBar(
                    isSelectMode = isSelectMode,
                    onMenuButtonClick = onMenuButtonClick,
                    onClearButtonClick = {
                        isSelectMode = false
                        isSelectedVideoItems.clear()
                    },
                )
            },
            bottomBar = {
                if (isSelectMode) {
                    VideoItemBottomAppBar(
                        shown = bottomBarShown,
                        onAllItemSelectButtonClick = {
                            (videoListUiState as? VideoListUiState.Success)?.videoItems?.let { videoItems ->
                                isSelectedVideoItems.putAll(
                                    videoItems.associateBy(
                                        keySelector = { it.id },
                                        valueTransform = { true }
                                    )
                                )
                            }
                        },
                        onTagSettingButtonClick = { onNavigateToTagSetting(isSelectedVideoItems.filterValues { it }.keys.toList()) },
                        onInfoButtonClick = { isShowVideoInfoDialog = true },
                        onClearSelectedVideoItems = {
                            isSelectMode = false
                            isSelectedVideoItems.clear()
                        },
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
                    if (isShowVideoInfoDialog) {
                        val selectedVideoItemIds = isSelectedVideoItems.filterValues { it }.keys
                        VideoInfoDialog(
                            videoItems = videoListUiState.videoItems.filter { selectedVideoItemIds.contains(it.id) },
                            onDismissRequest = { isShowVideoInfoDialog = false },
                            onConfirmButtonClick = { isShowVideoInfoDialog = false },
                        )
                    }
                    progressIndicatorState = false

                    val windowInfo = rememberWindowInfo()
                    when (windowInfo.screenWidthInfo) {
                        WindowInfo.WindowType.Compact -> {
                            CompactVideoList(
                                videoItems = videoListUiState.videoItems,
                                isSelectMode = { isSelectMode },
                                isSelectedVideoItems = isSelectedVideoItems,
                                onNavigateToPlayer = onNavigateToPlayer,
                                modifier = Modifier.padding(contentPadding),
                                contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
                                setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                                updateVideo = updateVideo,
                                onToggleVideoSelection = toggleVideoSelection,
                            )
                        }
                        else -> {
                            ExpandedVideoList(
                                videoItems = videoListUiState.videoItems,
                                isSelectMode = { isSelectMode },
                                isSelectedVideoItems = isSelectedVideoItems,
                                onNavigateToPlayer = onNavigateToPlayer,
                                modifier = Modifier.padding(contentPadding),
                                contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
                                setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                                updateVideo = updateVideo,
                                onToggleVideoSelection = toggleVideoSelection,
                            )
                        }
                    }
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
