package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.videolist.VideoListSortType

@Composable
fun VideoSortTypeMenu(
    expanded: Boolean,
    videoListSortTypeUiState: VideoListSortTypeUiState,
    onDismissRequest: () -> Unit,
    onSortTypeSet: (VideoListSortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        val sortTypeNames = stringArrayResource(id = R.array.videoSortTypes)
        videoListSortTypeUiState.sortTypes.forEach {
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        val iconSize = dimensionResource(id = R.dimen.video_sort_type_menu_icon_size)
                        Text(sortTypeNames[it.sortType.ordinal])
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
                        if (it.isSelected) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                            )
                            return@Row
                        }
                        Spacer(modifier = Modifier.width(iconSize))
                    }
                },
                onClick = { onSortTypeSet(it.sortType) },
            )
        }
    }
}
