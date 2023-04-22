package com.dogeby.tagplayer.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.RippleLoadingColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFilterChip(
    selected: Boolean,
    name: String,
    onVideoFilterAdd: () -> Unit,
    onVideoFilterRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = { if (selected) onVideoFilterRemove() else onVideoFilterAdd() },
        label = { Text(text = name) },
        modifier = modifier,
        leadingIcon = {
            AnimatedVisibility(selected) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RippleLoadingVideoFilterChip(
    modifier: Modifier = Modifier,
    color: Color = RippleLoadingColor,
    shape: Shape = FilterChipDefaults.shape,
    alpha: Float = rememberRippleLoadingEffectAlpha(),
    textStyle: TextStyle = LocalTextStyle.current,
) {
    FilterChip(
        selected = false,
        onClick = {},
        label = {
            Text(
                text = "",
                style = textStyle,
            )
        },
        modifier = modifier.widthIn(min = dimensionResource(id = R.dimen.rippleLoadingVideoFilterChip_minWidth)),
        enabled = false,
        shape = shape,
        colors = FilterChipDefaults.filterChipColors(disabledContainerColor = color.copy(alpha = alpha)),
    )
}

@Preview
@Composable
fun RippleLoadingVideoFilterChipPreview() {
    TagPlayerTheme {
        RippleLoadingVideoFilterChip()
    }
}
