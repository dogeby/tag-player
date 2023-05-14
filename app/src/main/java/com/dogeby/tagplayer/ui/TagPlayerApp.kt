package com.dogeby.tagplayer.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.dogeby.tagplayer.datastore.app.AppPreferencesData
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.ui.navigation.TagPlayerNavHost
import com.dogeby.tagplayer.ui.navigation.VideoListRoute
import com.dogeby.tagplayer.ui.permission.permissionNavigationRoute
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TagPlayerApp(
    appPreferencesData: AppPreferencesData,
    isRequiredPermissionsGranted: Boolean,
) {
    TagPlayerTheme(
        darkTheme = when (appPreferencesData.appThemeMode) {
            AppThemeMode.SYSTEM_SETTING -> { isSystemInDarkTheme() }
            AppThemeMode.LIGHT -> { false }
            AppThemeMode.DARK -> { true }
        },
    ) {
        TagPlayerNavHost(
            startDestination = if (isRequiredPermissionsGranted) VideoListRoute else permissionNavigationRoute,
        )
    }
}
