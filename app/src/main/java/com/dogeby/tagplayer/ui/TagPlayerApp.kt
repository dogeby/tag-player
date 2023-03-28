package com.dogeby.tagplayer.ui

import androidx.compose.runtime.Composable
import com.dogeby.tagplayer.ui.navigation.PermissionRoute
import com.dogeby.tagplayer.ui.navigation.TagPlayerNavHost
import com.dogeby.tagplayer.ui.navigation.VideoListRoute
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TagPlayerApp(
    onExit: () -> Unit = {},
    isRequiredPermissionsGranted: Boolean,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    TagPlayerTheme {
        TagPlayerNavHost(
            onExit = onExit,
            setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
            startDestination = if (isRequiredPermissionsGranted) VideoListRoute else PermissionRoute,
        )
    }
}
