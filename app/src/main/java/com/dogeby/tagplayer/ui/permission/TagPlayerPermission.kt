package com.dogeby.tagplayer.ui.permission

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.activity.findActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

private val readVideoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    Manifest.permission.READ_MEDIA_VIDEO
} else {
    Manifest.permission.READ_EXTERNAL_STORAGE
}

val AppRequiredPermission = listOf(readVideoPermission)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OptionalPermissionCheck(
    requireDescription: String,
    methodDescription: String,
    activityStartAction: String,
    permission: String,
    isDismissRequest: Boolean = false,
    onDismiss: () -> Unit = {},
) {
    val permissionState = rememberPermissionState(permission)

    if (permissionState.status.isGranted.not()) {
        PermissionAlertDialog(
            requireDescription = requireDescription,
            methodDescription = methodDescription,
            activityStartAction = activityStartAction,
            dismissButtonText = stringResource(id = R.string.cancel),
            isDismissRequest = isDismissRequest,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredPermissionsCheck(
    onDismiss: () -> Unit = {}
) {
    val permissionState = rememberMultiplePermissionsState(AppRequiredPermission)
    val context = LocalContext.current

    if (permissionState.allPermissionsGranted.not()) {
        PermissionAlertDialog(
            requireDescription = stringResource(id = R.string.permission_dialog_require_description),
            methodDescription = stringResource(id = R.string.permission_dialog_method_description),
            activityStartAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            dismissButtonText = stringResource(id = R.string.exit),
            onDismiss = {
                onDismiss()
                context.findActivity()?.finish()
            },
        )
    }
}

@Composable
fun WriteSettingsPermissionCheck(
    onDismiss: () -> Unit,
) {
    if (Settings.System.canWrite(LocalContext.current)) return
    OptionalPermissionCheck(
        requireDescription = stringResource(id = R.string.permission_dialog_optionalPermision_reason_description),
        methodDescription = stringResource(id = R.string.permission_dialog_optionalPermision_method_description),
        activityStartAction = Settings.ACTION_MANAGE_WRITE_SETTINGS,
        permission = Manifest.permission.WRITE_SETTINGS, // 이 권한은 isGranted 시 false를 반환한다.
        isDismissRequest = true,
        onDismiss = onDismiss,
    )
}
