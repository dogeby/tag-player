package com.dogeby.tagplayer.ui.videofilter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.component.VideoFilterChip
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
        onDirectoryFilterAdd = viewModel::addDirectoryFilter,
        onDirectoryFilterRemove = viewModel::removeDirectoryFilter,
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
    onDirectoryFilterAdd: (String) -> Unit,
    onDirectoryFilterRemove: (String) -> Unit,
    onTagFilterAdd: (Long) -> Unit,
    onTagFilterRemove: (Long) -> Unit,
    onQueryClear: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Scaffold(modifier = modifier) { contentPadding ->
        val layoutDirection = LocalLayoutDirection.current
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
            modifier = Modifier.padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = contentPadding.calculateBottomPadding(),
            ),
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
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoFilter_tagAndDirectoryEmpty),
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
                is VideoFilterUiState.Success -> {
                    LazyColumn {
                        item {
                            DirectoryFilterList(
                                directoryFilterUiState = videoFilterUiState.directoryFilterUiState,
                                onDirectoryFilterAdd = onDirectoryFilterAdd,
                                onDirectoryFilterRemove = onDirectoryFilterRemove,
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                            )
                        }
                        item {
                            TagFilterList(
                                tagFilterUiState = videoFilterUiState.tagFilterUiState,
                                onTagFilterAdd = onTagFilterAdd,
                                onTagFilterRemove = onTagFilterRemove,
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectoryFilterList(
    directoryFilterUiState: VideoDirectoryFilterUiState,
    onDirectoryFilterAdd: (String) -> Unit,
    onDirectoryFilterRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FilterTitle(
            name = stringResource(id = R.string.folder),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_folder),
                    contentDescription = null,
                )
            },
        )
        when (directoryFilterUiState) {
            VideoDirectoryFilterUiState.Empty -> {
                Text(
                    text = stringResource(id = R.string.videoFilter_directoryEmpty),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                )
            }
            is VideoDirectoryFilterUiState.Success -> {
                FlowRow {
                    directoryFilterUiState.directoryFilterItems.forEach { directoryFilter ->
                        VideoFilterChip(
                            selected = directoryFilter.isFiltered,
                            name = directoryFilter.name,
                            onVideoFilterAdd = { onDirectoryFilterAdd(directoryFilter.name) },
                            onVideoFilterRemove = { onDirectoryFilterRemove(directoryFilter.name) },
                            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagFilterList(
    tagFilterUiState: VideoTagFilterUiState,
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
        when (tagFilterUiState) {
            VideoTagFilterUiState.Empty -> {
                Text(
                    text = stringResource(id = R.string.videoFilter_tagEmpty),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                )
            }
            is VideoTagFilterUiState.Success -> {
                FlowRow {
                    tagFilterUiState.tagFilterItems.forEach { tagFilter ->
                        VideoFilterChip(
                            selected = tagFilter.isFilteredTag,
                            name = tagFilter.tagName,
                            onVideoFilterAdd = { onTagFilterAdd(tagFilter.tagId) },
                            onVideoFilterRemove = { onTagFilterRemove(tagFilter.tagId) },
                            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)),
                        )
                    }
                }
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
            tagFilterUiState = VideoTagFilterUiState.Success(
                List(8) {
                    VideoTagFilterItemUiState(
                        it.toLong(),
                        "Tag$it",
                        it % 2 == 0,
                    )
                },
            ),
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
