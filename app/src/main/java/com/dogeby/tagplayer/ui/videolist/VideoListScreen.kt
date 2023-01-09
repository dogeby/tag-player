package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoListSetting(
    filteredTagsUiState: FilteredTagsUiState,
    onNavigateToFilterSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTags = if (filteredTagsUiState is FilteredTagsUiState.Success) {
        filteredTagsUiState.filteredTagNames.joinToString(prefix = ": ")
    } else {
        ""
    }
    Row(modifier = modifier) {
        SettingAssistChip(
            text = "${stringResource(id = R.string.videolist_filter)}$filteredTags",
            onClick = onNavigateToFilterSetting,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_list),
                    contentDescription = null,
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAssistChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = modifier.widthIn(max = dimensionResource(id = R.dimen.videolist_setting_assist_chip_max_width)),
        leadingIcon = leadingIcon,
    )
}

@Preview(showBackground = true, heightDp = 640, widthDp = 360)
@Composable
fun VideoListSettingPreview() {
    TagPlayerTheme {
        VideoListSetting(
            filteredTagsUiState = FilteredTagsUiState.Success(List(4) { "tag$it" }),
            onNavigateToFilterSetting = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingAssistChipPreview() {
    TagPlayerTheme {
        SettingAssistChip(
            text = "필터: Media",
            onClick = { },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_list),
                    contentDescription = null,
                )
            },
        )
    }
}