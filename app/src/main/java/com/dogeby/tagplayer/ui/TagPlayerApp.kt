package com.dogeby.tagplayer.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.dogeby.tagplayer.datastore.app.AppPreferencesData
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.ui.navigation.PermissionRoute
import com.dogeby.tagplayer.ui.navigation.TagPlayerNavHost
import com.dogeby.tagplayer.ui.navigation.VideoListRoute
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TagPlayerApp(
    appPreferencesData: AppPreferencesData,
    onExit: () -> Unit = {},
    isRequiredPermissionsGranted: Boolean,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    TagPlayerTheme(
        darkTheme = when (appPreferencesData.appThemeMode) {
            AppThemeMode.SYSTEM_SETTING -> { isSystemInDarkTheme() }
            AppThemeMode.LIGHT -> { false }
            AppThemeMode.DARK -> { true }
        },
    ) {
        TagPlayerNavHost(
            onExit = onExit,
            setTopResumedActivityChangedListener = setTopResumedActivityChangedListener,
            startDestination = if (isRequiredPermissionsGranted) VideoListRoute else PermissionRoute,
        )
    }
}
