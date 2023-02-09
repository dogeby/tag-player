package com.dogeby.tagplayer.ui.tagsetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.ui.component.TagInputChipTextField
import com.dogeby.tagplayer.ui.component.TagManageMenuMoreHorizButton
import com.dogeby.tagplayer.ui.component.TagNameEditDialog
import com.dogeby.tagplayer.ui.component.VideoTag
import com.dogeby.tagplayer.ui.component.clearFocusWhenTap
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TagSettingRoute(
    modifier: Modifier = Modifier,
    viewModel: TagSettingViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
) {
    val tagInputChipTextFieldUiState: TagInputChipTextFieldUiState by viewModel.tagInputChipTextFieldUiState.collectAsState()
    val tagSearchResultUiState: TagSearchResultUiState by viewModel.tagSearchResultUiState.collectAsState()
    val tagNameEditDialogUiState by viewModel.tagNameEditDialogUiState.collectAsState()

    TagSettingScreen(
        tagInputChipTextFieldUiState = tagInputChipTextFieldUiState,
        tagSearchResultUiState = tagSearchResultUiState,
        tagNameEditDialogUiState = tagNameEditDialogUiState,
        modifier = modifier,
        onNavigateUp = onNavigateUp,
        onCreateTag = viewModel::createTag,
        onAddTagToVideo = viewModel::addTagToVideos,
        onTagChipClear = viewModel::removeTagFromVideos,
        onKeywordChange = viewModel::setTagSearchKeyword,
        onTagNameEdit = viewModel::modifyTagName,
        onTagDelete = viewModel::deleteTag,
        onShowTagNameEditDialog = viewModel::showTagNameEditDialog,
        onHideTagNameEditDialog = viewModel::hideTagNameEditDialog,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSettingScreen(
    tagInputChipTextFieldUiState: TagInputChipTextFieldUiState,
    tagSearchResultUiState: TagSearchResultUiState,
    tagNameEditDialogUiState: TagNameEditDialogUiState,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onCreateTag: (String) -> Unit = {},
    onAddTagToVideo: (Long) -> Unit = {},
    onTagChipClear: (Long) -> Unit = {},
    onKeywordChange: (String) -> Unit = {},
    onTagNameEdit: (Long, String) -> Unit = { _, _ -> },
    onTagDelete: (Long) -> Unit = {},
    onShowTagNameEditDialog: (Long, String) -> Unit = { _, _ -> },
    onHideTagNameEditDialog: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.clearFocusWhenTap(),
        topBar = { TagSettingTopAppBar(onArrowBackButtonClick = onNavigateUp) },
    ) { contentPadding ->
        LazyColumn(
            Modifier
                .wrapContentHeight()
                .padding(contentPadding),
        ) {
            item {
                if (tagNameEditDialogUiState is TagNameEditDialogUiState.Show) {
                    with(tagNameEditDialogUiState) {
                        TagNameEditDialog(
                            originalName = originalName,
                            isError = isError,
                            onEditButtonClick = { onTagNameEdit(tagId, it) },
                            onCancelButtonClick = onHideTagNameEditDialog,
                            onDismissRequest = onHideTagNameEditDialog,
                            supportingText = if (supportingTextResId != null) stringResource(id = supportingTextResId) else "",
                        )
                    }
                }
            }
            item {
                TagInputChipTextField(
                    keyword = tagInputChipTextFieldUiState.keyword,
                    onKeywordChange = onKeywordChange,
                    tags = tagInputChipTextFieldUiState.commonTags,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    onTagChipClear = onTagChipClear,
                )
            }
            item {
                Divider()
                Text(
                    text = stringResource(id = R.string.tagSetting_selectOrCreate),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            when (tagSearchResultUiState) {
                is TagSearchResultUiState.Success -> {
                    items(tagSearchResultUiState.tags) { tag ->
                        TagSettingSelectionItem(
                            tag = tag,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_small))
                                .fillMaxWidth(),
                            onClick = onAddTagToVideo,
                            onEditButtonClick = onShowTagNameEditDialog,
                            onDeleteButtonClick = onTagDelete,
                        )
                    }
                    if (tagSearchResultUiState.isShowTagCreateText) {
                        item {
                            TagCreateText(
                                keyword = tagSearchResultUiState.keyword,
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                                    .fillMaxWidth(),
                                onClick = onCreateTag,
                            )
                        }
                    }
                }
                TagSearchResultUiState.Empty -> {
                    item {
                        Text(
                            text = stringResource(id = R.string.tagSetting_tagSearchResultEmpty),
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                        )
                    }
                }
                TagSearchResultUiState.Loading -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSettingTopAppBar(
    modifier: Modifier = Modifier,
    onArrowBackButtonClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.tagSetting_topAppBarTitle)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onArrowBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
fun TagSettingSelectionItem(
    tag: TagSearchResultItemUiState,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit = {},
    onEditButtonClick: (Long, String) -> Unit = { _, _ -> },
    onDeleteButtonClick: (Long) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable {
                if (tag.isIncluded.not()) onClick(tag.id)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        VideoTag(
            name = tag.name,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .weight(1f, false)
                .align(Alignment.CenterVertically),
            shape = MaterialTheme.shapes.extraSmall,
            textStyle = MaterialTheme.typography.bodyLarge,
            isEllipsis = true,
        )
        TagManageMenuMoreHorizButton(
            onEditButtonClick = { onEditButtonClick(tag.id, tag.name) },
            onDeleteButtonClick = { onDeleteButtonClick(tag.id) },
        )
    }
}

@Composable
fun TagCreateText(
    keyword: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    Row(
        modifier = modifier
            .height(dimensionResource(id = R.dimen.tagSetting_tagCreateTextHeight))
            .clickable {
                onClick(keyword)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VideoTag(
            name = keyword,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .weight(1f, false)
                .align(Alignment.CenterVertically)
                .padding(end = dimensionResource(id = R.dimen.padding_small)),
            shape = MaterialTheme.shapes.extraSmall,
            textStyle = MaterialTheme.typography.bodyLarge,
            isEllipsis = true,
        )
        Text(
            text = stringResource(id = R.string.create),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagSettingScreenPreview() {
    TagPlayerTheme {
        TagSettingScreen(
            tagInputChipTextFieldUiState = TagInputChipTextFieldUiState(List(25) { Tag(it.toLong(), "Tag $it") }),
            tagSearchResultUiState = TagSearchResultUiState.Success(
                List(20) {
                    TagSearchResultItemUiState(
                        it.toLong(),
                        "Tag $it",
                        false,
                    )
                },
            ),
            tagNameEditDialogUiState = TagNameEditDialogUiState.Hide,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagCreateTextPreview() {
    TagPlayerTheme {
        TagCreateText(keyword = "Tag")
    }
}
