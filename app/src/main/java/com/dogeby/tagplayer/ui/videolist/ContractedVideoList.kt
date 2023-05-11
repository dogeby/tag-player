package com.dogeby.tagplayer.ui.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.video.VideoItem
import com.dogeby.tagplayer.ui.component.rememberRippleLoadingEffectAlpha
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun ContractedVideoList(
    videoItems: List<VideoItem>,
    isSelectMode: () -> Boolean,
    isSelectedVideoItems: Map<Long, Boolean>,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    onToggleVideoSelection: (VideoItem) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        header()
        if (videoItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight(0.5f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.videoList_listEmpty),
                    )
                }
            }
        }
        items(videoItems) { item ->
            ExpandedVideoCard(
                videoItem = item,
                isSelected = isSelectedVideoItems.getOrDefault(item.id, false),
                onClick = { videoItem ->
                    if (isSelectMode()) {
                        onToggleVideoSelection(videoItem)
                        return@ExpandedVideoCard
                    }
                    onNavigateToPlayer(videoItems.map { it.id }, videoItem.id)
                },
                onLongClick = onToggleVideoSelection,
                modifier = Modifier.padding(videoItemContentPadding),
            )
        }
        footer()
    }
}

@Composable
fun ContractedRippleLoadingVideoList(
    count: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    videoItemContentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        userScrollEnabled = false,
    ) {
        header()
        items(count) {
            ExpandedRippleLoadingVideoCard(
                modifier = Modifier.padding(videoItemContentPadding),
                containerRippleColor = containerRippleColor,
                contentRippleColor = contentRippleColor,
                rippleAlpha = rippleAlpha,
            )
        }
        footer()
    }
}

@Preview(showBackground = true)
@Composable
private fun ContractedRippleLoadingVideoListPreview() {
    TagPlayerTheme {
        ContractedRippleLoadingVideoList(5)
    }
}
