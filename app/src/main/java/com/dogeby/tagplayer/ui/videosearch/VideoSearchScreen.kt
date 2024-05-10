package com.dogeby.tagplayer.ui.videosearch

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.component.WindowInfo
import com.dogeby.tagplayer.ui.component.clearFocusWhenTap
import com.dogeby.tagplayer.ui.component.rememberWindowInfo
import com.dogeby.tagplayer.ui.videolist.CompactVideoList
import com.dogeby.tagplayer.ui.videolist.ContractedVideoList
import com.dogeby.tagplayer.ui.videolist.ExpandedVideoList
import com.dogeby.tagplayer.ui.videolist.VideoInfoDialog
import com.dogeby.tagplayer.ui.videolist.VideoItemBottomAppBar

@Composable
fun VideoSearchRoute(
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoSearchViewModel = hiltViewModel(),
) {
    val query: String by viewModel.query.collectAsState()
    val videoSearchViewUiState: VideoSearchViewUiState by viewModel.videoSearchViewUiState.collectAsState()
    val videoListInitialItemIndex: Int by viewModel
        .videoListInitialManager
        .videoListInitialItemIndex
        .collectAsState()

    VideoSearchScreen(
        videoSearchViewUiState = videoSearchViewUiState,
        query = query,
        videoListInitialItemIndex = videoListInitialItemIndex,
        onQueryChange = viewModel::setQuery,
        onNavigateToPlayer = onNavigateToPlayer,
        onClear = viewModel::clearQuery,
        onNavigateUp = onNavigateUp,
        onNavigateToTagSetting = onNavigateToTagSetting,
        modifier = modifier,
        onSaveVideoListInitialItemIndex = viewModel.videoListInitialManager::setVideoListInitialItemIndex,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VideoSearchScreen(
    videoSearchViewUiState: VideoSearchViewUiState,
    query: String,
    videoListInitialItemIndex: Int,
    onQueryChange: (String) -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onClear: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    onSaveVideoListInitialItemIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val windowInfo = rememberWindowInfo()

    var isSelectMode by remember(videoSearchViewUiState) {
        mutableStateOf(false)
    }
    val isSelectedVideoItems = remember(videoSearchViewUiState) {
        mutableStateMapOf<Long, Boolean>()
    }
    val toggleVideoSelection = remember(videoSearchViewUiState) {
        { videoItem: VideoItem ->
            focusManager.clearFocus()
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
                if (query.isNotEmpty()) {
                    onClear()
                    return@SearchBar
                }
                onNavigateUp()
            },
            modifier = Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                    bottom = contentPadding.calculateBottomPadding(),
                )
                .clearFocusWhenTap(),
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

                    when (windowInfo.screenWidthInfo) {
                        WindowInfo.WindowType.Contracted -> {
                            ContractedVideoList(
                                videoItems = videoSearchViewUiState.videoListUiState.videoItems,
                                isSelectMode = { isSelectMode },
                                isSelectedVideoItems = isSelectedVideoItems,
                                firstVisibleItemIndex = videoListInitialItemIndex,
                                onNavigateToPlayer = onNavigateToPlayer,
                                contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
                                onToggleVideoSelection = toggleVideoSelection,
                                onSaveVideoListInitialItemIndex = onSaveVideoListInitialItemIndex,
                            )
                        }
                        WindowInfo.WindowType.Compact -> {
                            CompactVideoList(
                                videoItems = videoSearchViewUiState.videoListUiState.videoItems,
                                isSelectMode = { isSelectMode },
                                isSelectedVideoItems = isSelectedVideoItems,
                                firstVisibleItemIndex = videoListInitialItemIndex,
                                onNavigateToPlayer = onNavigateToPlayer,
                                contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
                                onToggleVideoSelection = toggleVideoSelection,
                                onSaveVideoListInitialItemIndex = onSaveVideoListInitialItemIndex,
                            )
                        }
                        else -> {
                            ExpandedVideoList(
                                videoItems = videoSearchViewUiState.videoListUiState.videoItems,
                                isSelectMode = { isSelectMode },
                                isSelectedVideoItems = isSelectedVideoItems,
                                firstVisibleItemIndex = videoListInitialItemIndex,
                                onNavigateToPlayer = onNavigateToPlayer,
                                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                                verticalItemSpacing = 0.dp,
                                videoItemContentPadding = PaddingValues(vertical = dimensionResource(id = R.dimen.padding_small) / 2),
                                onToggleVideoSelection = toggleVideoSelection,
                                onSaveVideoListInitialItemIndex = onSaveVideoListInitialItemIndex,
                            )
                        }
                    }
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
