package com.dogeby.tagplayer.ui.navigation

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
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.permission.AppPermissionDeniedByExternalAction
import com.dogeby.tagplayer.ui.permission.PermissionScreen
import com.dogeby.tagplayer.ui.taglist.TagListRoute
import com.dogeby.tagplayer.ui.tagsetting.TagSettingRoute
import com.dogeby.tagplayer.ui.videofilter.VideoFilterRoute
import com.dogeby.tagplayer.ui.videolist.VideoListRoute
import com.dogeby.tagplayer.ui.videoplayer.VideoPlayerRoute
import com.dogeby.tagplayer.ui.videosearch.VideoSearchRoute
import com.google.gson.Gson

@Composable
fun TagPlayerNavHost(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = PermissionRoute,
    setTopResumedActivityChangedListener: ((((isTopResumedActivity: Boolean) -> Unit)?) -> Unit)? = null,
) {
    val drawerItems = listOf(
        TagPlayerDrawerItem(
            route = VideoListRoute,
            name = stringResource(id = R.string.videoList_topAppBar_title),
            icon = ImageVector.vectorResource(id = R.drawable.ic_movie),
        ),
        TagPlayerDrawerItem(
            route = TagListRoute,
            name = stringResource(id = R.string.tagList_topAppBar_title),
            icon = ImageVector.vectorResource(id = R.drawable.ic_tag),
        ),
    )
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        composable(PermissionRoute) {
            PermissionScreen(
                onNavigateToDestination = {
                    navController.navigate(VideoListRoute) {
                        popUpTo(PermissionRoute) { inclusive = true }
                    }
                },
            )
        }
        composable(VideoListRoute) {
            AppPermissionDeniedByExternalAction(onExit)
            VideoListRoute(
                tagPlayerDrawerItems = drawerItems,
                onNavigateToRoute = { navController.navigate(it.route) },
                onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
                onNavigateToFilterSetting = { navController.navigate(VideoFilterRoute) },
                onNavigateToTagSetting = { videoIds ->
                    navController.navigate("$TagSettingRoute/${Gson().toJson(videoIds)}")
                },
                onNavigateToVideoSearch = { navController.navigate(VideoSearchRoute) },
                setTopResumedActivityChangedListener = setTopResumedActivityChangedListener
            )
        }
        composable(
            route = "$TagSettingRoute/{$TagSettingVideoIdsArgument}",
            arguments = listOf(navArgument(TagSettingVideoIdsArgument) { type = NavType.StringType }),
        ) {
            TagSettingRoute(
                modifier = modifier,
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(VideoSearchRoute) {
            AppPermissionDeniedByExternalAction(onExit)
            VideoSearchRoute(
                onNavigateToPlayer = { videoIds, videoIndex -> navController.navigate("$VideoPlayerRoute/${Gson().toJson(videoIds)}/$videoIndex") },
                onNavigateUp = { navController.navigateUp() },
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
                onNavigateToRoute = {
                    navController.navigate(it) {
                        popUpTo(VideoListRoute) { inclusive = true }
                    }
                },
                onNavigateToTagDetail = { /*TODO*/ },
            )
        }
    }
}
