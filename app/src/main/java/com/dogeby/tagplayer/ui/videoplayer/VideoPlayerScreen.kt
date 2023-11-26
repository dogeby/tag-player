package com.dogeby.tagplayer.ui.videoplayer

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.activity.findActivity
import com.dogeby.tagplayer.ui.theme.PlayerBackgroundColor
import com.dogeby.tagplayer.ui.theme.PlayerControllerBackgroundColor
import com.dogeby.tagplayer.ui.theme.PlayerControllerOnBackgroundColor
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun VideoPlayerRoute(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel = hiltViewModel(),
) {
    val videoPlayerPagerUiState: VideoPlayerPagerUiState by viewModel.videoPlayerPagerUiState.collectAsState()
    val useSystemAutoRotation by viewModel.useSystemAutoRotation.collectAsState()
    VideoPlayerScreen(
        videoPlayerPagerUiState = videoPlayerPagerUiState,
        onPlayerSettledPageChanged = viewModel::onPlayerSettledPageChanged,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
        useSystemAutoRotation = useSystemAutoRotation,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VideoPlayerScreen(
    videoPlayerPagerUiState: VideoPlayerPagerUiState,
    onPlayerSettledPageChanged: (videoId: Long) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    useSystemAutoRotation: Boolean = false,
) {
    val systemUiController = rememberSystemUiController()
    val activity = LocalContext.current.findActivity()

    DisposableEffect(systemUiController) {
        systemUiController.isSystemBarsVisible = false
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            systemUiController.isSystemBarsVisible = true
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    var videoPlayerControllerVisible by remember {
        mutableStateOf(true)
    }

    Scaffold(
        topBar = {
            VideoPlayerControllerAnimation(visible = videoPlayerControllerVisible) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = PlayerControllerOnBackgroundColor,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PlayerControllerBackgroundColor,
                        scrolledContainerColor = PlayerControllerBackgroundColor,
                        navigationIconContentColor = PlayerControllerOnBackgroundColor,
                        titleContentColor = PlayerControllerOnBackgroundColor,
                        actionIconContentColor = PlayerControllerOnBackgroundColor,
                    )
                )
            }
        },
    ) {
        when (videoPlayerPagerUiState) {
            VideoPlayerPagerUiState.Loading -> {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PlayerBackgroundColor,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            VideoPlayerPagerUiState.Empty -> {
                val message = stringResource(id = R.string.videoPlayer_videoEmpty)
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                onNavigateUp()
            }

            is VideoPlayerPagerUiState.Success -> {
                VideoPlayerPager(
                    currentPageVideoId = videoPlayerPagerUiState.currentVideoId,
                    videoItems = videoPlayerPagerUiState.videoItems,
                    isControllerVisible = { videoPlayerControllerVisible },
                    onSettledPageChanged = onPlayerSettledPageChanged,
                    modifier = modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                        ) {
                            videoPlayerControllerVisible = videoPlayerControllerVisible.not()
                        },
                    useSystemAutoRotation = useSystemAutoRotation,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerScreenLoadingPreview() {
    TagPlayerTheme {
        VideoPlayerScreen(
            videoPlayerPagerUiState = VideoPlayerPagerUiState.Loading,
            onPlayerSettledPageChanged = {},
            onNavigateUp = {},
        )
    }
}
