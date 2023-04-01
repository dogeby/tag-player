package com.dogeby.tagplayer.ui.videosearch

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.videolist.VideoList

@Composable
fun VideoSearchRoute(
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoSearchViewModel = hiltViewModel(),
) {
    val query: String by viewModel.query.collectAsState()
    val videoSearchViewUiState: VideoSearchViewUiState by viewModel.videoSearchViewUiState.collectAsState()

    VideoSearchScreen(
        videoSearchViewUiState = videoSearchViewUiState,
        query = query,
        onQueryChange = viewModel::setQuery,
        onVideoClick = onNavigateToPlayer,
        onClear = viewModel::clearQuery,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSearchScreen(
    videoSearchViewUiState: VideoSearchViewUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onVideoClick: (List<Long>, Long) -> Unit,
    onClear: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
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
            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
            leadingIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            placeholder = {
                Text(text = stringResource(id = R.string.videoSearch_searchTextFieldHint))
            },
            trailingIcon = {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            },
        ) {
            when (videoSearchViewUiState) {
                is VideoSearchViewUiState.Success -> {
                    Text(
                        text = stringResource(R.string.search_result),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                    VideoList(
                        videoListUiState = videoSearchViewUiState.videoListUiState,
                        isSelectMode = false,
                        isSelectedVideoItems = emptyMap(),
                        onNavigateToPlayer = onVideoClick,
                        onToggleVideoItem = {},
                    )
                }
                VideoSearchViewUiState.QueryBlank -> {
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoSearch_queryBlank),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
                VideoSearchViewUiState.Empty -> {
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.videoSearch_searchResultEmpty),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                    )
                }
            }
        }
    }
}
