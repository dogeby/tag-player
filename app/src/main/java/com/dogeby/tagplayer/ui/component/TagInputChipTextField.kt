package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputChipTextField(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    tags: List<Tag>,
    modifier: Modifier = Modifier,
    onTagChipClick: (Long) -> Unit = {},
    onTagChipClear: (Long) -> Unit = {},
) {
    InputChipTextField(
        keyword = keyword,
        isInputChipIncluded = tags.isNotEmpty(),
        modifier = modifier,
        onKeywordChange = onKeywordChange,
    ) {
        tags.forEach { tag ->
            InputChip(
                selected = false,
                onClick = { onTagChipClick(tag.id) },
                label = {
                    Text(
                        text = tag.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                modifier = Modifier.padding(
                    end = dimensionResource(R.dimen.padding_small),
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { onTagChipClear(tag.id) },
                        modifier = Modifier.size(InputChipDefaults.IconSize),
                    ) {
                        Icon(Icons.Default.Clear, null)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputChipTextField(
    keyword: String,
    isInputChipIncluded: Boolean,
    modifier: Modifier = Modifier,
    onKeywordChange: (String) -> Unit = {},
    content: @Composable () -> Unit,
) {
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
        Box(
            modifier = Modifier
                .defaultMinSize(
                    minHeight = dimensionResource(id = R.dimen.inputChipTextField_textField_minHeight),
                ),
        ) {
            BasicTextField(
                value = keyword,
                onValueChange = {
                    onKeywordChange(it)
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .focusRequester(keywordTextFieldFocusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = {
                    if (isInputChipIncluded.not() && keyword.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.tagInputChipTextField_searchHint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        it()
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagInputChipTextFieldPreview() {
    TagPlayerTheme {
        var keyword by rememberSaveable {
            mutableStateOf("")
        }
        TagInputChipTextField(
            keyword = keyword,
            onKeywordChange = { keyword = it },
            tags = List(19) { Tag(it.toLong(), "Tag$it$it") },
        )
    }
}
