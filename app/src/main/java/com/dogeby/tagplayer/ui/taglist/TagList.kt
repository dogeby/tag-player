package com.dogeby.tagplayer.ui.taglist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.tag.TagItem
import com.dogeby.tagplayer.ui.component.RippleLoadingText
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoThumbnail
import com.dogeby.tagplayer.ui.component.VideoThumbnail
import com.dogeby.tagplayer.ui.component.rememberRippleLoadingEffectAlpha
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagList(
    tagItems: List<TagItem>,
    onTagItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dimensionResource(id = R.dimen.tagListItem_Width)),
        modifier = modifier,
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            items = tagItems,
            key = { it.id },
        ) { tagItem ->
            TagListItem(
                tagItem = tagItem,
                thumbnailSize = DpSize(
                    dimensionResource(id = R.dimen.videoSmallThumbnail_width),
                    dimensionResource(id = R.dimen.videoSmallThumbnail_height)
                ),
                onClick = onTagItemClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListItem(
    tagItem: TagItem,
    thumbnailSize: DpSize,
    onClick: (tagId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(tagItem.id) },
        modifier = modifier
    ) {
        if (tagItem.videoItems.isNotEmpty()) {
            VideoThumbnail(
                uri = tagItem.videoItems.first().uri,
                width = thumbnailSize.width,
                height = thumbnailSize.height,
                modifier = Modifier.aspectRatio(16 / 9f),
                applyContentBasedColorToBackgroundColor = true,
                contentScale = ContentScale.FillHeight,
            )
        }
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = tagItem.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.video_count, tagItem.videoItems.size),
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RippleLoadingTagList(
    count: Int,
    modifier: Modifier = Modifier,
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(dimensionResource(id = R.dimen.tagListItem_Width)),
        modifier = modifier,
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false,
    ) {
        items(count) {
            RippleLoadingTagListItem(
                containerRippleColor = containerRippleColor,
                contentRippleColor = contentRippleColor,
                rippleAlpha = rippleAlpha,
            )
        }
    }
}

@Composable
fun RippleLoadingTagListItem(
    modifier: Modifier = Modifier,
    containerRippleColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentRippleColor: Color = RippleLoadingColor,
    rippleAlpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerRippleColor.copy(alpha = rippleAlpha)
        ),
    ) {
        RippleLoadingVideoThumbnail(
            modifier = Modifier
                .aspectRatio(16 / 9f),
            alpha = rippleAlpha,
        )
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            RippleLoadingText(
                modifier = Modifier.fillMaxWidth(0.8f),
                color = contentRippleColor,
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            RippleLoadingText(
                modifier = Modifier.fillMaxWidth(0.3f),
                color = contentRippleColor,
                textStyle = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagListPreview() {
    TagPlayerTheme {
        TagList(
            tagItems = List(3) { TagItem(it.toLong(), "tag", emptyList()) },
            onTagItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagListItemPreview() {
    TagPlayerTheme {
        TagListItem(
            tagItem = TagItem(
                id = 0,
                name = "태그",
                videoItems = emptyList()
            ),
            thumbnailSize = DpSize(165.dp, 100.dp),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RippleLoadingTagListPreview() {
    MaterialTheme {
        RippleLoadingTagList(4)
    }
}

@Preview(showBackground = true)
@Composable
fun RippleLoadingTagListItemPreview() {
    MaterialTheme {
        RippleLoadingTagListItem()
    }
}
