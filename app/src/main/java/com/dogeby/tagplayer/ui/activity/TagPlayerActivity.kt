package com.dogeby.tagplayer.ui.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dogeby.tagplayer.ui.TagPlayerApp
import com.dogeby.tagplayer.ui.permission.AppRequiredPermission
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagPlayerActivity : AppCompatActivity() {

    private val isRequiredPermissionsGranted: Boolean
        get() = AppRequiredPermission.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

    private val viewModel: TagPlayerViewModel by viewModels()
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.appPreferencesData.value == null
            }
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        if (isRequiredPermissionsGranted) {
                            viewModel.updateVideoList()
                        }
                    }
                }
            )
        }

        setContent {
            val appPreferencesData by viewModel.appPreferencesData.collectAsState()
            appPreferencesData?.let { preferencesData ->
                checkAppUpdate(preferencesData.rejectedUpdateVersionCode)
                TagPlayerApp(
                    appPreferencesData = preferencesData,
                    isRequiredPermissionsGranted = isRequiredPermissionsGranted,
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        if (isTopResumedActivity && isRequiredPermissionsGranted) viewModel.updateVideoList()
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            activityResultRegistry.register(
                APP_UPDATE_KEY,
                ActivityResultContracts.StartIntentSenderForResult(),
            ) {
                if (it.resultCode == RESULT_CANCELED) {
                    viewModel.setRejectedUpdateVersionCode(appUpdateInfo.availableVersionCode())
                }
            },
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
        )
    }

    private fun checkAppUpdate(rejectedUpdateVersionCode: Int) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) &&
                appUpdateInfo.availableVersionCode() > rejectedUpdateVersionCode
            ) {
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    companion object {
        private const val APP_UPDATE_KEY = "app_update_key"
    }
}

fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

fun Activity.setKeepScreenOn(isKeepScreenOn: Boolean) {
    if (isKeepScreenOn) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return
    }
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
