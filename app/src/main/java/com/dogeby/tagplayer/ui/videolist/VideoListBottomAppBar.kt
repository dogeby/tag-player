package com.dogeby.tagplayer.ui.videolist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimation
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimationIconButton
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

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
                onClick = onSortButtonClick,
                isShowAnimation = isShowActionIconAnimation,
                delayMillis = 300,
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_sort), contentDescription = null)
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
            onSearchButtonClick = { },
            onFilterButtonClick = { },
            onSortButtonClick = { },
            isShowActionIconAnimation = true,
        )
    }
}
