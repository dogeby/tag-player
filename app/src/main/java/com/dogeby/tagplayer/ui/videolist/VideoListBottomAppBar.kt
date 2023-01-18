package com.dogeby.tagplayer.ui.videolist

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
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
        BottomAppBarIconButton(
            iconResId = R.drawable.ic_search,
            onClick = onSearchButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 400f,
        )
        BottomAppBarIconButton(
            iconResId = if (isFilterButtonChecked) R.drawable.ic_filled_filter else R.drawable.ic_outlined_filter,
            onClick = onFilterButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 200f,
        )
        BottomAppBarIconButton(
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
        BottomAppBarIconButton(
            iconResId = R.drawable.ic_all_select,
            onClick = onAllItemSelectButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 400f,
        )
        BottomAppBarIconButton(
            iconResId = R.drawable.ic_tag,
            onClick = onTagSettingButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 200f,
        )
        BottomAppBarIconButton(
            iconResId = R.drawable.ic_info,
            onClick = onInfoButtonClick,
            isShowAnimation = isShowAnimation,
            stiffness = 120f,
        )
    }
}

@Composable
fun BottomAppBarIconButton(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    isShowAnimation: Boolean = false,
    stiffness: Float = Spring.StiffnessMedium,
) {
    val transitionState = remember {
        MutableTransitionState(isShowAnimation.not()).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visibleState = transitionState,
        enter = slideInVertically(
            animationSpec = spring(stiffness = stiffness),
            initialOffsetY = { it },
        )
    ) {
        IconButton(onClick = onClick) {
            Icon(painter = painterResource(id = iconResId), contentDescription = contentDescription)
        }
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
