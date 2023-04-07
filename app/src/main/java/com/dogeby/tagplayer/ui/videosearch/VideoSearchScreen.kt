package com.dogeby.tagplayer.ui.videosearch

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.dogeby.tagplayer.ui.videolist.VideoInfoDialog
import com.dogeby.tagplayer.ui.videolist.VideoItemBottomAppBar
import com.dogeby.tagplayer.ui.videolist.VideoList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoSearchRoute(
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoSearchViewModel = hiltViewModel(),
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    val query: String by viewModel.query.collectAsState()
    val videoSearchViewUiState: VideoSearchViewUiState by viewModel.videoSearchViewUiState.collectAsState()

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) viewModel.updateVideoList()

    VideoSearchScreen(
        videoSearchViewUiState = videoSearchViewUiState,
        query = query,
        onQueryChange = viewModel::setQuery,
        onNavigateToPlayer = onNavigateToPlayer,
        onClear = viewModel::clearQuery,
        onNavigateUp = onNavigateUp,
        onNavigateToTagSetting = onNavigateToTagSetting,
        modifier = modifier,
        setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
        updateVideo = viewModel::updateVideoList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSearchScreen(
    videoSearchViewUiState: VideoSearchViewUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onClear: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null
) {
    var isSelectMode by remember(videoSearchViewUiState) {
        mutableStateOf(false)
    }
    val isSelectedVideoItems = remember(videoSearchViewUiState) {
        mutableStateMapOf<Long, Boolean>()
    }
    val toggleVideoSelection = remember(videoSearchViewUiState) {
        { videoItem: VideoItem ->
            isSelectedVideoItems.compute(videoItem.id) { _, v ->
                v?.not() ?: true
            }
            isSelectMode = isSelectedVideoItems.all { it.value.not() }.not()
        }
    }
    val isBottomBarShow by rememberSaveable { mutableStateOf(false) }.apply {
        this.value = isSelectMode
    }
    var isShowVideoInfoDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        bottomBar = {
            VideoItemBottomAppBar(
                shown = isBottomBarShow,
                onAllItemSelectButtonClick = {
                    (videoSearchViewUiState as? VideoSearchViewUiState.Success)?.videoListUiState?.videoItems?.let { videoItems ->
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
                isShowActionIconAnimation = true,
            )
        },
    ) { contentPadding ->
        val layoutDirection = LocalLayoutDirection.current
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                focusManager.clearFocus()
                onSearch(it)
            },
            active = true,
            onActiveChange = { active ->
                if (active) return@SearchBar
                if (isSelectMode) {
                    isSelectMode = false
                    isSelectedVideoItems.clear()
                    return@SearchBar
                }
                onNavigateUp()
            },
            modifier = Modifier.padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = contentPadding.calculateBottomPadding(),
            ),
            leadingIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            placeholder = {
                Text(text = stringResource(id = R.string.videoSearch_searchTextFieldHint))
            },
            trailingIcon = {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            },
        ) {
            when (videoSearchViewUiState) {
                is VideoSearchViewUiState.Success -> {
                    Text(
                        text = stringResource(R.string.search_result),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                    if (isShowVideoInfoDialog) {
                        val selectedVideoItemIds = isSelectedVideoItems.filterValues { it }.keys
                        VideoInfoDialog(
                            videoItems = videoSearchViewUiState.videoListUiState.videoItems.filter {
                                selectedVideoItemIds.contains(it.id)
                            },
                            onDismissRequest = { isShowVideoInfoDialog = false },
                            onConfirmButtonClick = { isShowVideoInfoDialog = false },
                        )
                    }
                    VideoList(
                        videoItems = videoSearchViewUiState.videoListUiState.videoItems,
                        isSelectMode = { isSelectMode },
                        isSelectedVideoItems = isSelectedVideoItems,
                        onNavigateToPlayer = onNavigateToPlayer,
                        contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
                        setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                        updateVideo = updateVideo,
                        onToggleVideoSelection = toggleVideoSelection,
                    )
                }
                VideoSearchViewUiState.QueryBlank -> {
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoSearch_queryBlank),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
                VideoSearchViewUiState.Empty -> {
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoSearch_searchResultEmpty),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
            }
        }
    }
}
