package com.dogeby.tagplayer.ui.tagdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.ExpandedTagDetailCard
import com.dogeby.tagplayer.ui.component.TagDetailCard
import com.dogeby.tagplayer.ui.component.TagNameEditDialog
import com.dogeby.tagplayer.ui.component.WindowInfo
import com.dogeby.tagplayer.ui.component.rememberWindowInfo
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.dogeby.tagplayer.ui.tagsetting.TagNameEditDialogUiState
import com.dogeby.tagplayer.ui.videolist.VideoInfoDialog
import com.dogeby.tagplayer.ui.videolist.VideoItemBottomAppBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TagDetailRoute(
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TagDetailViewModel = hiltViewModel(),
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    val tagDetailUiState by viewModel.tagDetailUiState.collectAsState()
    val tagNameEditDialogUiState by viewModel.tagNameEditDialogUiState.collectAsState()

    val permissionState: PermissionState = rememberPermissionState(AppRequiredPermission)
    if (permissionState.status.isGranted) viewModel.updateVideoList()

    TagDetailScreen(
        tagDetailUiState = tagDetailUiState,
        tagNameEditDialogUiState = tagNameEditDialogUiState,
        onTagNameEditDialogVisibilitySet = viewModel::setTagNameEditDialogVisibility,
        onEditTagName = viewModel::modifyTagName,
        onDeleteTag = viewModel::deleteTag,
        onNavigateUp = onNavigateUp,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToTagSetting = onNavigateToTagSetting,
        modifier = modifier,
        setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
        updateVideo = viewModel::updateVideoList
    )
}

@Composable
fun TagDetailScreen(
    tagDetailUiState: TagDetailUiState,
    tagNameEditDialogUiState: TagNameEditDialogUiState,
    onTagNameEditDialogVisibilitySet: (visibility: Boolean) -> Unit,
    onEditTagName: (String) -> Unit,
    onDeleteTag: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateToTagSetting: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
    updateVideo: (() -> Unit)? = null
) {
    var isSelectMode by remember(tagDetailUiState) {
        mutableStateOf(false)
    }
    val isSelectedVideoItems = remember(tagDetailUiState) {
        mutableStateMapOf<Long, Boolean>()
    }
    val toggleVideoSelection = remember(tagDetailUiState) {
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

    Scaffold(
        modifier = modifier,
        bottomBar = {
            VideoItemBottomAppBar(
                shown = isBottomBarShow,
                onAllItemSelectButtonClick = {
                    (tagDetailUiState as? TagDetailUiState.Success)?.videoItems?.let { videoItems ->
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
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) { contentPadding ->
        when (tagDetailUiState) {
            is TagDetailUiState.Success -> {
                if (isShowVideoInfoDialog) {
                    val selectedVideoItemIds = isSelectedVideoItems.filterValues { it }.keys
                    VideoInfoDialog(
                        videoItems = tagDetailUiState.videoItems.filter {
                            selectedVideoItemIds.contains(it.id)
                        },
                        onDismissRequest = { isShowVideoInfoDialog = false },
                        onConfirmButtonClick = { isShowVideoInfoDialog = false },
                    )
                }

                val windowInfo = rememberWindowInfo()
                when (windowInfo.screenWidthInfo) {
                    WindowInfo.WindowType.Compact -> {
                        TagDetailCard(
                            tagName = { tagDetailUiState.tagName },
                            videoItems = { tagDetailUiState.videoItems },
                            thumbnailSize = DpSize(165.dp, 100.dp),
                            onPlayButtonClick = onNavigateToPlayer,
                            onEditButtonClick = { onTagNameEditDialogVisibilitySet(true) },
                            onDeleteButtonClick = {
                                onDeleteTag()
                                onNavigateUp()
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding),
                            isSelectMode = { isSelectMode },
                            isSelectedVideoItems = isSelectedVideoItems,
                            setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                            updateVideo = updateVideo,
                            onToggleVideoSelection = toggleVideoSelection,
                        )
                    }
                    else -> {
                        val layoutDirection = LocalLayoutDirection.current
                        ExpandedTagDetailCard(
                            tagName = { tagDetailUiState.tagName },
                            videoItems = { tagDetailUiState.videoItems },
                            onPlayButtonClick = onNavigateToPlayer,
                            onEditButtonClick = { onTagNameEditDialogVisibilitySet(true) },
                            onDeleteButtonClick = {
                                onDeleteTag()
                                onNavigateUp()
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding),
                            isSelectMode = { isSelectMode },
                            isSelectedVideoItems = isSelectedVideoItems,
                            setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
                            updateVideo = updateVideo,
                            onToggleVideoSelection = toggleVideoSelection,
                        )
                    }
                }
            }
            TagDetailUiState.Loading -> { /*TODO*/ }
            TagDetailUiState.Empty -> { /*TODO*/ }
        }
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier
                .padding(contentPadding)
                .padding(top = 8.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }

        when (tagNameEditDialogUiState) {
            is TagNameEditDialogUiState.Show -> {
                val supportingText = tagNameEditDialogUiState.supportingTextResId?.let {
                    stringResource(
                        id = it,
                    )
                }
                TagNameEditDialog(
                    originalName = tagNameEditDialogUiState.originalName,
                    isError = tagNameEditDialogUiState.isError,
                    onEditButtonClick = onEditTagName,
                    onCancelButtonClick = { onTagNameEditDialogVisibilitySet(false) },
                    supportingText = supportingText.orEmpty(),
                )
            }
            TagNameEditDialogUiState.Hide -> Unit
        }
    }
}
