package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TagManageMenuMoreVertButton(
    onTagEdit: () -> Unit,
    onTagDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TagManageMenuMoreButton(
        onTagEdit = onTagEdit,
        onTagDelete = onTagDelete,
        modifier = modifier,
    ) {
        Icon(Icons.Default.MoreVert, contentDescription = null)
    }
}

@Composable
fun TagManageMenuMoreHorizButton(
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TagManageMenuMoreButton(
        onTagEdit = onEditButtonClick,
        onTagDelete = onDeleteButtonClick,
        modifier = modifier,
    ) {
        Icon(painterResource(id = R.drawable.ic_more_horiz), contentDescription = null)
    }
}

@Composable
fun TagManageMenuMoreButton(
    onTagEdit: () -> Unit,
    onTagDelete: () -> Unit,
    modifier: Modifier = Modifier,
    moreIconButtonContent: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart),
    ) {
        IconButton(onClick = { expanded = true }) {
            moreIconButtonContent()
        }
        TagManageMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onEditMenuItemClick = {
                expanded = false
                onTagEdit()
            },
            onDeleteMenuItemClick = {
                expanded = false
                onTagDelete()
            },
        )
    }
}

@Composable
fun TagManageMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditMenuItemClick: () -> Unit,
    onDeleteMenuItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.edit)) },
            onClick = onEditMenuItemClick,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = null,
                )
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.delete)) },
            onClick = onDeleteMenuItemClick,
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagManageMenuMoreButtonPreview() {
    TagPlayerTheme {
        TagManageMenuMoreVertButton(
            onTagEdit = {},
            onTagDelete = {},
        )
    }
}
