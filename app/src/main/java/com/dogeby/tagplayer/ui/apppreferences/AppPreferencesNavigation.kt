package com.dogeby.tagplayer.ui.apppreferences

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions

const val appPreferencesNavigationRoute = "app_preferences_route"

fun NavController.navigateToAppPreferences(navOptions: NavOptions? = null) {
    navigate(
        route = appPreferencesNavigationRoute,
        navOptions = navOptions,
    )
}

fun NavController.navigateToAppPreferences(builder: NavOptionsBuilder.() -> Unit) {
    navigateToAppPreferences(navOptions = navOptions(builder))
}

fun NavGraphBuilder.appPreferences(
    onNavigateUp: () -> Unit,
) {
    composable(appPreferencesNavigationRoute) {
        AppPreferencesRoute(onNavigateUp = onNavigateUp)
    }
}
