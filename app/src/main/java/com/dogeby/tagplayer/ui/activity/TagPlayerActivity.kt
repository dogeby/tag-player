package com.dogeby.tagplayer.ui.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.dogeby.tagplayer.ui.TagPlayerApp
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagPlayerActivity : ComponentActivity() {

    private val isRequiredPermissionsGranted: Boolean
        get() = checkSelfPermission(AppRequiredPermission) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    private var topResumedActivityChangedListener: ((isTopResumedActivity: Boolean) -> Unit)? = null

    private val viewModel: TagPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.appPreferencesData.value == null
            }
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val appPreferencesData by viewModel.appPreferencesData.collectAsState()
            appPreferencesData?.let { preferencesData ->
                TagPlayerApp(
                    appPreferencesData = preferencesData,
                    onExit = { finish() },
                    setTopResumedActivityChangedListener =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        { topResumedActivityChangedListener = it }
                    } else {
                        null
                    },
                    isRequiredPermissionsGranted = isRequiredPermissionsGranted,
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        topResumedActivityChangedListener?.let { it(isTopResumedActivity) }
    }
}

fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}