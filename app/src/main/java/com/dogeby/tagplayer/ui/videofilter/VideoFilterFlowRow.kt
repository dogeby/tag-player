package com.dogeby.tagplayer.ui.videofilter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.RippleLoadingVideoFilterChip
import com.dogeby.tagplayer.ui.component.rememberRippleLoadingEffectAlpha
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoFilterFlowRow(
    filterTitleName: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Column(modifier = modifier) {
        FilterTitle(
            name = filterTitleName,
            leadingIcon = leadingIcon,
        )
        FlowRow {
            content()
        }
    }
}

@Composable
fun FilterTitle(
    name: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Row(modifier = modifier) {
        if (leadingIcon != null) {
            leadingIcon()
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = name)
    }
}

@Composable
fun RippleLoadingVideoFilterFlowRow(
    filterTitleName: String,
    itemCount: Int,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    color: Color = RippleLoadingColor,
    alpha: Float = rememberRippleLoadingEffectAlpha(),
) {
    VideoFilterFlowRow(
        filterTitleName = filterTitleName,
        modifier = modifier,
        leadingIcon = leadingIcon,
    ) {
        repeat(itemCount) {
            RippleLoadingVideoFilterChip(
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)),
                color = color,
                alpha = alpha,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTitlePreview() {
    TagPlayerTheme {
        FilterTitle(
            name = "태그",
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = null,
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RippleLoadingVideoFilterFlowRowPreview() {
    TagPlayerTheme {
        RippleLoadingVideoFilterFlowRow(
            filterTitleName = "title",
            itemCount = 7,
        )
    }
}
