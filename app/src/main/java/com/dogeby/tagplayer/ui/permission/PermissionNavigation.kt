package com.dogeby.tagplayer.ui.permission

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions

const val permissionNavigationRoute = "permission_route"

fun NavController.navigateToPermission(navOptions: NavOptions? = null) {
    navigate(
        route = permissionNavigationRoute,
        navOptions = navOptions,
    )
}

fun NavController.navigateToPermission(builder: NavOptionsBuilder.() -> Unit) {
    navigateToPermission(navOptions = navOptions(builder))
}

fun NavGraphBuilder.permissionScreen(
    onNavigateToDestination: () -> Unit,
) {
    composable(permissionNavigationRoute) {
        PermissionScreen(onNavigateToDestination = onNavigateToDestination)
    }
}
