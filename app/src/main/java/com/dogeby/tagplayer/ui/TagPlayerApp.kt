package com.dogeby.tagplayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dogeby.tagplayer.ui.navigation.PermissionRoute
import com.dogeby.tagplayer.ui.navigation.TagPlayerNavHost
import com.dogeby.tagplayer.ui.navigation.VideoListRoute
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPlayerApp(
    isRequiredPermissionsGranted: Boolean
) {
    TagPlayerTheme {
        Scaffold { contentPadding ->
            TagPlayerNavHost(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                startDestination = if (isRequiredPermissionsGranted) VideoListRoute else PermissionRoute,
            )
        }
    }
}
