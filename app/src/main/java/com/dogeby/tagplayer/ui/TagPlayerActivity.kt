package com.dogeby.tagplayer.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagPlayerActivity : ComponentActivity() {

    private val isRequiredPermissionsGranted: Boolean
        get() = checkSelfPermission(AppRequiredPermission) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            TagPlayerApp(isRequiredPermissionsGranted)
        }
    }
}
