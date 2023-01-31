package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputChipTextField(
    tags: List<Tag>,
    modifier: Modifier = Modifier,
    onTagChipClick: (Long) -> Unit = {},
    onTagChipClear: (Long) -> Unit = {},
    onKeywordChange: (String) -> Unit = {},
) {
    InputChipTextField(
        modifier = modifier,
        onKeywordChange = onKeywordChange,
    ) {
        tags.forEach { tag ->
            InputChip(
                selected = false,
                onClick = { onTagChipClick(tag.id) },
                label = { Text(tag.name) },
                modifier = Modifier.padding(
                    end = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_medium),
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { onTagChipClear(tag.id) },
                    ) {
                        Icon(Icons.Default.Clear, null)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InputChipTextField(
    modifier: Modifier = Modifier,
    onKeywordChange: (String) -> Unit = {},
    content: @Composable () -> Unit,
) {
    var keyword by rememberSaveable {
        mutableStateOf("")
    }
    val keywordTextFieldFocusRequester = remember {
        FocusRequester()
    }

    FlowRow(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                keywordTextFieldFocusRequester.requestFocus()
            },
    ) {
        content()
        BasicTextField(
            value = keyword,
            onValueChange = {
                keyword = it
                onKeywordChange(it)
            },
            modifier = Modifier
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight,
                )
                .focusRequester(keywordTextFieldFocusRequester),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagInputChipTextFieldPreview() {
    TagPlayerTheme {
        TagInputChipTextField(
            tags = List(19) { Tag(it.toLong(), "Tag$it$it") },
        )
    }
}
