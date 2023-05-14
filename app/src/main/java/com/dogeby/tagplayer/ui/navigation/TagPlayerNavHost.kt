package com.dogeby.tagplayer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.apppreferences.AppPreferencesRoute
import com.dogeby.tagplayer.ui.component.TagPlayerDrawerItem
import com.dogeby.tagplayer.ui.permission.permissionNavigationRoute
import com.dogeby.tagplayer.ui.permission.permissionScreen
import com.dogeby.tagplayer.ui.tagdetail.navigateToTagDetail
import com.dogeby.tagplayer.ui.tagdetail.tagDetailScreen
import com.dogeby.tagplayer.ui.taglist.navigateToTagList
import com.dogeby.tagplayer.ui.taglist.tagListNavigationRoute
import com.dogeby.tagplayer.ui.taglist.tagListScreen
import com.dogeby.tagplayer.ui.tagsetting.navigateToTagSetting
import com.dogeby.tagplayer.ui.tagsetting.tagSettingScreen
import com.dogeby.tagplayer.ui.videofilter.navigateToVideoFilter
import com.dogeby.tagplayer.ui.videofilter.videoFilterScreen
import com.dogeby.tagplayer.ui.videolist.navigateToVideoList
import com.dogeby.tagplayer.ui.videolist.videoListNavigationRoute
import com.dogeby.tagplayer.ui.videolist.videoListScreen
import com.dogeby.tagplayer.ui.videoplayer.navigateToVideoPlayer
import com.dogeby.tagplayer.ui.videoplayer.videoPlayerScreen
import com.dogeby.tagplayer.ui.videosearch.navigateToVideoSearch
import com.dogeby.tagplayer.ui.videosearch.videoSearchScreen

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
            route = tagListNavigationRoute,
            name = stringResource(id = R.string.tagList_topAppBar_title),
            icon = ImageVector.vectorResource(id = R.drawable.ic_tag),
            onClick = { navController.navigateToTagList() },
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
            onNavigateToPlayer = { videoIds, videoIndex ->
                navController.navigateToVideoPlayer(
                    startVideoId = videoIndex,
                    videoIds = videoIds,
                )
            },
            onNavigateToFilterSetting = { navController.navigateToVideoFilter() },
            onNavigateToTagSetting = { videoIds ->
                navController.navigateToTagSetting(videoIds)
            },
            onNavigateToVideoSearch = { navController.navigateToVideoSearch() },
        )
        tagSettingScreen(onNavigateUp = { navController.navigateUp() })
        videoSearchScreen(
            onNavigateUp = { navController.navigateUp() },
            onNavigateToPlayer = { videoIds, videoIndex ->
                navController.navigateToVideoPlayer(
                    startVideoId = videoIndex,
                    videoIds = videoIds,
                )
            },
            onNavigateToTagSetting = { videoIds ->
                navController.navigateToTagSetting(videoIds)
            },
        )
        videoFilterScreen(onNavigateUp = { navController.navigateUp() })
        videoPlayerScreen(onNavigateUp = { navController.navigateUp() })
        tagListScreen(
            tagPlayerDrawerItems = drawerItems,
            onNavigateToTagDetail = { tagId ->
                navController.navigateToTagDetail(tagId)
            },
        )
        tagDetailScreen(
            onNavigateUp = { navController.navigateUp() },
            onNavigateToPlayer = { videoIds, videoIndex ->
                navController.navigateToVideoPlayer(
                    startVideoId = videoIndex,
                    videoIds = videoIds,
                )
            },
            onNavigateToTagSetting = { videoIds ->
                navController.navigateToTagSetting(videoIds)
            },
        )
        composable(AppPreferencesRoute) {
            AppPreferencesRoute(
                onNavigateUp = { navController.navigateUp() },
            )
        }
    }
}
