package com.dogeby.tagplayer.ui.taglist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.MaxSizeCenterText
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.component.TagPlayerNavigationDrawer
import kotlinx.coroutines.launch

@Composable
fun TagListRoute(
    tagPlayerDrawerItems: List<TagPlayerDrawerItem>,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToTagDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TagListViewModel = hiltViewModel(),
) {
    val tagListUiState by viewModel.tagListUiState.collectAsState()
    val tagCreateDialogUiState by viewModel.tagCreateDialogUiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    TagPlayerNavigationDrawer(
        startRoute = com.dogeby.tagplayer.ui.navigation.TagListRoute,
        tagPlayerDrawerItems = tagPlayerDrawerItems,
        onItemClick = { onNavigateToRoute(it.route) },
        drawerState = drawerState,
    ) {
        TagListScreen(
            tagListUiState = tagListUiState,
            tagCreateDialogUiState = tagCreateDialogUiState,
            onCreateDialogVisibilitySet = viewModel::setTagCreateDialogVisibility,
            onCreateTag = viewModel::createTag,
            onNavigateToTagDetail = onNavigateToTagDetail,
            onMenuClick = { scope.launch { drawerState.open() } },
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagListScreen(
    tagListUiState: TagListUiState,
    tagCreateDialogUiState: TagCreateDialogUiState,
    onCreateDialogVisibilitySet: (visibility: Boolean) -> Unit,
    onCreateTag: (String) -> Unit,
    onNavigateToTagDetail: (Long) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        Scaffold(
            modifier = modifier,
            topBar = { TagListTopAppBar(onMenuClick = onMenuClick) },
            floatingActionButton = {
                val density = LocalDensity.current
                val layoutDirection = LocalLayoutDirection.current
                val windowInsets = ScaffoldDefaults.contentWindowInsets
                val leftWindowInsets = density.run { windowInsets.getLeft(density, layoutDirection).toDp() }
                val rightWindowInsets = density.run { windowInsets.getRight(density, layoutDirection).toDp() }

                FloatingActionButton(
                    onClick = { onCreateDialogVisibilitySet(true) },
                    modifier = Modifier.padding(
                        start = if (layoutDirection == LayoutDirection.Ltr) leftWindowInsets else rightWindowInsets,
                        end = if (layoutDirection == LayoutDirection.Ltr) rightWindowInsets else leftWindowInsets
                    ),
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            },
        ) { contentPadding ->
            when (tagListUiState) {
                is TagListUiState.Success -> {
                    TagList(
                        tagItems = tagListUiState.tagItems,
                        onTagItemClick = onNavigateToTagDetail,
                        modifier = Modifier
                            .padding(contentPadding)
                            .padding(8.dp)
                    )
                }
                TagListUiState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(contentPadding),
                    )
                }
                TagListUiState.Empty -> {
                    MaxSizeCenterText(
                        text = stringResource(id = R.string.tagList_empty),
                    )
                }
            }
            when (tagCreateDialogUiState) {
                is TagCreateDialogUiState.Show -> {
                    val supportingText = tagCreateDialogUiState.supportingTextResId?.let { stringResource(id = it) }
                    TagCreateDialog(
                        isError = { tagCreateDialogUiState.isError },
                        supportingText = { supportingText.orEmpty() },
                        onCreateButtonClick = onCreateTag,
                        onCancelButtonClick = { onCreateDialogVisibilitySet(false) },
                    )
                }
                TagCreateDialogUiState.Hide -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListTopAppBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.tagList_topAppBar_title)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
    )
}
