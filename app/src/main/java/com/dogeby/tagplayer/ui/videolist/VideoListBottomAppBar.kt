package com.dogeby.tagplayer.ui.videolist

import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.BottomAppBarAnimationIconButton
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoListBottomAppBar(
    isFilterButtonChecked: Boolean,
    onSearchButtonClick: () -> Unit,
    onFilterButtonClick: () -> Unit,
    onSortButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isShowAnimation: Boolean = true,
) {
    BottomAppBar(
        modifier = modifier,
    ) {
        BottomAppBarAnimationIconButton(
            iconResId = R.drawable.ic_search,
            onClick = onSearchButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 400f,
        )
        BottomAppBarAnimationIconButton(
            iconResId = if (isFilterButtonChecked) R.drawable.ic_filled_filter else R.drawable.ic_outlined_filter,
            onClick = onFilterButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 200f,
        )
        BottomAppBarAnimationIconButton(
            iconResId = R.drawable.ic_sort,
            onClick = onSortButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 120f,
        )
    }
}

@Composable
fun VideoItemBottomAppBar(
    onAllItemSelectButtonClick: () -> Unit,
    onTagSettingButtonClick: () -> Unit,
    onInfoButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isShowAnimation: Boolean = true,
) {
    BottomAppBar(
        modifier = modifier,
    ) {
        BottomAppBarAnimationIconButton(
            iconResId = R.drawable.ic_all_select,
            onClick = onAllItemSelectButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 400f,
        )
        BottomAppBarAnimationIconButton(
            iconResId = R.drawable.ic_tag,
            onClick = onTagSettingButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 200f,
        )
        BottomAppBarAnimationIconButton(
            iconResId = R.drawable.ic_info,
            onClick = onInfoButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 120f,
        )
    }
}

@Preview
@Composable
fun VideoListBottomAppBar() {
    TagPlayerTheme {
        VideoListBottomAppBar(
            isFilterButtonChecked = true,
            onSearchButtonClick = { },
            onFilterButtonClick = { },
            onSortButtonClick = { },
            isShowAnimation = true,
        )
    }
}
