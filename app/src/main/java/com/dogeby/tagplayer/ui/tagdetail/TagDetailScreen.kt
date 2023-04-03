package com.dogeby.tagplayer.ui.tagdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.ui.component.TagDetailCard
import com.dogeby.tagplayer.ui.component.TagNameEditDialog
import com.dogeby.tagplayer.ui.tagsetting.TagNameEditDialogUiState

@Composable
fun TagDetailRoute(
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TagDetailViewModel = hiltViewModel(),
) {
    val tagDetailUiState by viewModel.tagDetailUiState.collectAsState()
    val tagNameEditDialogUiState by viewModel.tagNameEditDialogUiState.collectAsState()

    TagDetailScreen(
        tagDetailUiState = tagDetailUiState,
        tagNameEditDialogUiState = tagNameEditDialogUiState,
        onTagNameEditDialogVisibilitySet = viewModel::setTagNameEditDialogVisibility,
        onEditTagName = viewModel::modifyTagName,
        onDeleteTag = viewModel::deleteTag,
        onNavigateUp = onNavigateUp,
        onNavigateToPlayer = onNavigateToPlayer,
        modifier = modifier,
    )
}

@Composable
fun TagDetailScreen(
    tagDetailUiState: TagDetailUiState,
    tagNameEditDialogUiState: TagNameEditDialogUiState,
    onTagNameEditDialogVisibilitySet: (visibility: Boolean) -> Unit,
    onEditTagName: (String) -> Unit,
    onDeleteTag: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: (List<Long>, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) { contentPadding ->
        when (tagDetailUiState) {
            is TagDetailUiState.Success -> {
                TagDetailCard(
                    tagName = { tagDetailUiState.tagName },
                    videoItems = { tagDetailUiState.videoItems },
                    thumbnailSize = DpSize(165.dp, 100.dp),
                    onPlayButtonClick = onNavigateToPlayer,
                    onEditButtonClick = { onTagNameEditDialogVisibilitySet(true) },
                    onDeleteButtonClick = {
                        onDeleteTag()
                        onNavigateUp()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            TagDetailUiState.Loading -> { /*TODO*/ }
            TagDetailUiState.Empty -> { /*TODO*/ }
        }
        IconButton(
            onClick = onNavigateUp,
            modifier = Modifier.padding(contentPadding).padding(top = 8.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }

        when (tagNameEditDialogUiState) {
            is TagNameEditDialogUiState.Show -> {
                val supportingText = tagNameEditDialogUiState.supportingTextResId?.let {
                    stringResource(
                        id = it,
                    )
                }
                TagNameEditDialog(
                    originalName = tagNameEditDialogUiState.originalName,
                    isError = tagNameEditDialogUiState.isError,
                    onEditButtonClick = onEditTagName,
                    onCancelButtonClick = { onTagNameEditDialogVisibilitySet(false) },
                    supportingText = supportingText.orEmpty(),
                )
            }
            TagNameEditDialogUiState.Hide -> Unit
        }
    }
}
