package com.dogeby.tagplayer.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import kotlinx.coroutines.launch

@Composable
fun TagPlayerNavigationDrawer(
    startRoute: String,
    tagPlayerDrawerItems: List<TagPlayerDrawerItem>,
    onItemClick: (TagPlayerDrawerItem) -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf(startRoute) }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(270.dp)
            ) {
                Spacer(Modifier.height(12.dp))
                tagPlayerDrawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.name) },
                        selected = item.route == selectedItem,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem = item.route
                            onItemClick(item)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        content = content,
    )
}

data class TagPlayerDrawerItem(
    val route: String,
    val name: String,
    val icon: ImageVector,
    val contentDescription: String? = null,
)

@Preview(showBackground = true)
@Composable
fun TagPlayerNavigationDrawerPreview() {
    TagPlayerTheme {
        val items = List(3) {
            TagPlayerDrawerItem(it.toString(), it.toString(), Icons.Default.Favorite)
        }
        TagPlayerNavigationDrawer(
            startRoute = items.first().route,
            tagPlayerDrawerItems = items,
            onItemClick = {},
        ) {}
    }
}
