package com.dogeby.tagplayer.ui.videolist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimation
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimationIconButton
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import kotlin.math.roundToInt

@Composable
fun VideoListBottomAppBar(
    shown: Boolean,
    isFilterButtonChecked: Boolean,
    videoListSortTypeUiState: VideoListSortTypeUiState,
    onSearchButtonClick: () -> Unit,
    onFilterButtonClick: () -> Unit,
    onSortButtonClick: () -> Unit,
    onSortTypeSet: (VideoListSortType) -> Unit,
    modifier: Modifier = Modifier,
    isShowActionIconAnimation: Boolean = true,
    bottomBarOffsetHeightPx: (() -> Float) = { 0f }
) {
    var sortTypeMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val animatedBottomBarOffsetHeightPx by animateFloatAsState(bottomBarOffsetHeightPx())

    BottomAppBarAnimation(
        shown = shown,
    ) {
        BottomAppBar(
            modifier = modifier.offset {
                IntOffset(
                    x = 0,
                    y = -animatedBottomBarOffsetHeightPx.roundToInt()
                )
            },
        ) {
            BottomAppBarAnimationIconButton(
                onClick = onSearchButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 100,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                )
            }
            BottomAppBarAnimationIconButton(
                onClick = onFilterButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 200,
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFilterButtonChecked) R.drawable.ic_filled_filter else R.drawable.ic_outlined_filter,
                    ),
                    contentDescription = null,
                )
            }
            BottomAppBarAnimationIconButton(
                onClick = {
                    onSortButtonClick()
                    sortTypeMenuExpanded = true
                },
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 300,
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_sort), contentDescription = null)
                    VideoSortTypeMenu(
                        expanded = sortTypeMenuExpanded,
                        videoListSortTypeUiState = videoListSortTypeUiState,
                        onDismissRequest = { sortTypeMenuExpanded = false },
                        onSortTypeSet = onSortTypeSet
                    )
                }
            }
        }
    }
}

@Composable
fun VideoItemBottomAppBar(
    shown: Boolean,
    onAllItemSelectButtonClick: () -> Unit,
    onTagSettingButtonClick: () -> Unit,
    onInfoButtonClick: () -> Unit,
    onClearSelectedVideoItems: () -> Unit,
    modifier: Modifier = Modifier,
    isShowActionIconAnimation: Boolean = true,
) {

    BackHandler(
        enabled = shown,
        onBack = onClearSelectedVideoItems
    )

    BottomAppBarAnimation(
        shown = shown,
    ) {
        BottomAppBar(
            modifier = modifier,
        ) {
            BottomAppBarAnimationIconButton(
                onClick = onAllItemSelectButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 100,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_all_select),
                    contentDescription = null,
                )
            }
            BottomAppBarAnimationIconButton(
                onClick = onTagSettingButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 200,
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_tag), contentDescription = null)
            }
            BottomAppBarAnimationIconButton(
                onClick = onInfoButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 300,
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_info), contentDescription = null)
            }
        }
    }
}

@Preview
@Composable
fun VideoListBottomAppBarPreview() {
    TagPlayerTheme {
        VideoListBottomAppBar(
            shown = true,
            isFilterButtonChecked = true,
            videoListSortTypeUiState = VideoListSortTypeUiState(emptyList()),
            onSearchButtonClick = { },
            onFilterButtonClick = { },
            onSortButtonClick = { },
            onSortTypeSet = { },
            isShowActionIconAnimation = true,
        )
    }
}
