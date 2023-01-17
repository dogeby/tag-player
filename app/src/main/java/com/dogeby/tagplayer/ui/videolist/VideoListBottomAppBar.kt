package com.dogeby.tagplayer.ui.videolist

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
) {
    BottomAppBar(
        modifier = modifier,
    ) {
        IconButton(onClick = onSearchButtonClick) {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
        }
        IconButton(onClick = onFilterButtonClick) {
            if (isFilterButtonChecked) {
                Icon(painter = painterResource(id = R.drawable.ic_filled_filter), contentDescription = null)
            } else {
                Icon(painter = painterResource(id = R.drawable.ic_outlined_filter), contentDescription = null)
            }
        }
        IconButton(onClick = onSortButtonClick,) {
            Icon(painter = painterResource(id = R.drawable.ic_sort), contentDescription = null)
        }
    }
}

@Composable
fun VideoItemBottomAppBar(
    onAllItemSelectButtonClick: () -> Unit,
    onTagSettingButtonClick: () -> Unit,
    onInfoButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
    ) {
        IconButton(onClick = onAllItemSelectButtonClick) {
            Icon(painter = painterResource(id = R.drawable.ic_all_select), contentDescription = null)
        }
        IconButton(onClick = onTagSettingButtonClick) {
            Icon(painter = painterResource(id = R.drawable.ic_tag), contentDescription = null)
        }
        IconButton(onClick = onInfoButtonClick,) {
            Icon(painter = painterResource(id = R.drawable.ic_info), contentDescription = null)
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
        )
    }
}
