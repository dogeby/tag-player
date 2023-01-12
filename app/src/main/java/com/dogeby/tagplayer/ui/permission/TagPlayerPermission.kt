package com.dogeby.tagplayer.ui.permission

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dogeby.tagplayer.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

val AppRequiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    Manifest.permission.READ_MEDIA_VIDEO
} else {
    Manifest.permission.READ_EXTERNAL_STORAGE
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppPermissionDeniedByExternalAction(
    onExitClick: () -> Unit = {}
) {
    val permissionState = rememberPermissionState(
        permission = AppRequiredPermission
    )
    if (permissionState.status.isGranted.not()) {
        PermissionAlertDialog(
            dismissButtonText = stringResource(id = R.string.exit),
            onDismissRequest = onExitClick
        )
    }
}
