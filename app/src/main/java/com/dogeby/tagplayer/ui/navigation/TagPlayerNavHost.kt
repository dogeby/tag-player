package com.dogeby.tagplayer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.apppreferences.AppPreferencesRoute
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.permission.AppPermissionCheck
import com.dogeby.tagplayer.ui.permission.permissionNavigationRoute
import com.dogeby.tagplayer.ui.permission.permissionScreen
import com.dogeby.tagplayer.ui.tagdetail.TagDetailRoute
import com.dogeby.tagplayer.ui.taglist.TagListRoute
import com.dogeby.tagplayer.ui.tagsetting.TagSettingRoute
import com.dogeby.tagplayer.ui.videofilter.VideoFilterRoute
import com.dogeby.tagplayer.ui.videolist.navigateToVideoList
import com.dogeby.tagplayer.ui.videolist.videoListNavigationRoute
import com.dogeby.tagplayer.ui.videolist.videoListScreen
import com.dogeby.tagplayer.ui.videoplayer.VideoPlayerRoute
import com.dogeby.tagplayer.ui.videosearch.VideoSearchRoute
import com.google.gson.Gson

@Composable
fun TagPlayerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = permissionNavigationRoute,
) {
    val drawerItems = listOf(
        TagPlayerDrawerItem(
            route = videoListNavigationRoute,
            name = stringResource(id = R.string.videoList_topAppBar_title),
            icon = ImageVector.vectorResource(id = R.drawable.ic_movie),
            onClick = {
                navController.navigateToVideoList {
                    popUpTo(videoListNavigationRoute) { inclusive = true }
                }
            }
        ),
        TagPlayerDrawerItem(
            route = TagListRoute,
            name = stringResource(id = R.string.tagList_topAppBar_title),
            icon = ImageVector.vectorResource(id = R.drawable.ic_tag),
            onClick = { navController.navigate(TagListRoute) },
        ),
        TagPlayerDrawerItem(
            route = AppPreferencesRoute,
            name = stringResource(id = R.string.appPreferences_topAppBar_title),
            icon = Icons.Default.Settings,
            onClick = { navController.navigate(AppPreferencesRoute) },
        ),
    )
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        permissionScreen(
            onNavigateToDestination = {
                navController.navigateToVideoList {
                    popUpTo(permissionNavigationRoute) { inclusive = true }
                }
            },
        )
        videoListScreen(
            tagPlayerDrawerItems = drawerItems,
            onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
            onNavigateToFilterSetting = { navController.navigate(VideoFilterRoute) },
            onNavigateToTagSetting = { videoIds ->
                navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
            },
            onNavigateToVideoSearch = { navController.navigate(VideoSearchRoute) },
        )
        composable(
            route = "$TagSettingRoute/{$TagSettingVideoIdsArgument}",
            arguments = listOf(navArgument(TagSettingVideoIdsArgument) { type = NavType.StringType }),
        ) {
            TagSettingRoute(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(VideoSearchRoute) {
            AppPermissionCheck()
            VideoSearchRoute(
                onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
                onNavigateUp = { navController.navigateUp() },
                onNavigateToTagSetting = { videoIds ->
                    navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
                },
            )
        }
        composable(VideoFilterRoute) {
            VideoFilterRoute(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = "$VideoPlayerRoute/{$VideoPlayerVideoIdsArgument}/{$VideoPlayerStartVideoId}",
            arguments = listOf(
                navArgument(VideoPlayerVideoIdsArgument) { type = NavType.StringType },
                navArgument(VideoPlayerStartVideoId) { type = NavType.LongType },
            )
        ) {
            VideoPlayerRoute(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(TagListRoute) {
            TagListRoute(
                tagPlayerDrawerItems = drawerItems,
                onNavigateToTagDetail = { navController.navigate("$TagDetailRoute/$it") },
            )
        }
        composable(
            route = "$TagDetailRoute/{$TagDetailTagIdArgument}",
            arguments = listOf(navArgument(TagDetailTagIdArgument) { type = NavType.LongType }),
        ) {
            TagDetailRoute(
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
                onNavigateToTagSetting = { videoIds ->
                    navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
                },
            )
        }
        composable(AppPreferencesRoute) {
            AppPreferencesRoute(
                onNavigateUp = { navController.navigateUp() },
            )
        }
    }
}
