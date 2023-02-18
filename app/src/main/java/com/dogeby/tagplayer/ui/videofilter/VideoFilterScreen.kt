package com.dogeby.tagplayer.ui.videofilter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun VideoFilterRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoFilterViewModel = hiltViewModel(),
) {
    val query: String by viewModel.query.collectAsState()
    val videoFilterUiState: VideoFilterUiState by viewModel.videoFilterUiState.collectAsState()

    VideoFilterScreen(
        videoFilterUiState = videoFilterUiState,
        query = query,
        onQueryChange = viewModel::setQuery,
        onTagFilterAdd = viewModel::addTagFilter,
        onTagFilterRemove = viewModel::removeTagFilter,
        onQueryClear = viewModel::clearQuery,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFilterScreen(
    videoFilterUiState: VideoFilterUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onTagFilterAdd: (Long) -> Unit,
    onTagFilterRemove: (Long) -> Unit,
    onQueryClear: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Scaffold(modifier = modifier) { contentPadding ->
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                focusManager.clearFocus()
                onSearch(it)
            },
            active = true,
            onActiveChange = {
                if (it.not()) onNavigateUp()
            },
            modifier = Modifier.padding(contentPadding),
            leadingIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            placeholder = {
                Text(text = stringResource(id = R.string.videoFilter_searchTextFieldHint))
            },
            trailingIcon = {
                IconButton(onClick = onQueryClear) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            },
        ) {
            when (videoFilterUiState) {
                VideoFilterUiState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
                VideoFilterUiState.Empty -> {
                    Text(
                        text = stringResource(id = R.string.videoFilter_tagEmpty),
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
                is VideoFilterUiState.Success -> {
                    TagFilterList(
                        tagFilters = videoFilterUiState.tagFilters,
                        onTagFilterAdd = onTagFilterAdd,
                        onTagFilterRemove = onTagFilterRemove,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagFilterList(
    tagFilters: List<VideoTagFilterUiState>,
    onTagFilterAdd: (Long) -> Unit,
    onTagFilterRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FilterTitle(
            name = stringResource(id = R.string.tag),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = null,
                )
            },
        )
        FlowRow {
            tagFilters.forEach { tagFilter ->
                FilterChip(
                    selected = tagFilter.isFilteredTag,
                    onClick = {
                        if (tagFilter.isFilteredTag) {
                            onTagFilterRemove(tagFilter.tagId)
                        } else {
                            onTagFilterAdd(tagFilter.tagId)
                        }
                    },
                    label = { Text(text = tagFilter.tagName) },
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)),
                    leadingIcon = {
                        AnimatedVisibility(tagFilter.isFilteredTag) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        }
                    },
                )
            }
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

@Preview(showBackground = true)
@Composable
fun TagFilterListPreview() {
    TagPlayerTheme {
        TagFilterList(
            tagFilters = List(8) {
                VideoTagFilterUiState(
                    it.toLong(),
                    "Tag$it",
                    it % 2 == 0,
                )
            },
            onTagFilterAdd = {},
            onTagFilterRemove = {},
        )
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
