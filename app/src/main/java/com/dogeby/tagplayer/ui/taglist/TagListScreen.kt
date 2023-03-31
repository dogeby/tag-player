package com.dogeby.tagplayer.ui.taglist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R

@Composable
fun TagListRoute(
    onNavigateToTagDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TagListViewModel = hiltViewModel(),
) {
    val tagListUiState by viewModel.tagListUiState.collectAsState()

    TagListScreen(
        tagListUiState = tagListUiState,
        onNavigateToTagDetail = onNavigateToTagDetail,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagListScreen(
    tagListUiState: TagListUiState,
    onNavigateToTagDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        Scaffold(
            modifier = modifier,
            topBar = { TagListTopAppBar() },
        ) { contentPadding ->
            when (tagListUiState) {
                TagListUiState.Empty -> { /*TODO*/ }
                TagListUiState.Loading -> { /*TODO*/ }
                is TagListUiState.Success -> {
                    TagList(
                        tagItems = tagListUiState.tagItems,
                        onTagItemClick = onNavigateToTagDetail,
                        modifier = Modifier.padding(contentPadding).padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListTopAppBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.tagList_topAppBar_title)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
    )
}
