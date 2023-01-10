package com.dogeby.tagplayer.ui.permission

import android.Manifest
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.BuildConfig
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
private fun PermissionElement(
    @DrawableRes image: Int,
    @StringRes title: Int,
    @StringRes description: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = image),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(dimensionResource(id = R.dimen.permission_element_image_size)),
        )
        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
        ) {
            Text(
                text = stringResource(id = title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(id = description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun PermissionAlertDialog(
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit,
    dialogState: MutableState<Boolean> = rememberSaveable { mutableStateOf(true) },
) {
    if (dialogState.value) {
        AlertDialog(
            modifier = modifier.padding(all = dimensionResource(id = R.dimen.padding_medium)),
            onDismissRequest = { dialogState.value = false },
            text = {
                Text(
                    text = "${stringResource(id = R.string.permission_dialog_require_description)}\n\n${
                    stringResource(id = R.string.permission_dialog_method_description)
                    }",
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmClick,
                ) {
                    Text(text = stringResource(id = R.string.permission_dialog_app_setting))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogState.value = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onNavigateToDestination: () -> Unit,
) {
    val permissionDialogState = rememberSaveable { mutableStateOf(false) }
    if (permissionDialogState.value) {
        val context = LocalContext.current
        PermissionAlertDialog(
            onConfirmClick = {
                context.startActivity(
                    Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID),
                    ),
                )
            },
            dialogState = permissionDialogState,
        )
    }

    val permissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        },
    )
    if (permissionState.status.isGranted) {
        permissionDialogState.value = false
        onNavigateToDestination()
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(all = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.permission_guide_app_access_permission),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.permission_screen_display_bottom_padding)),
        )
        Text(
            text = stringResource(id = R.string.permission_app_permission_request_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.permission_request_description_bottom_padding)),
        )

        PermissionElement(
            image = R.drawable.ic_media_video_permission,
            title = R.string.permission_read_media_video,
            description = R.string.permission_read_media_video_description,
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (permissionState.status.shouldShowRationale) {
                    permissionDialogState.value = true
                } else {
                    permissionState.launchPermissionRequest()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(text = stringResource(id = R.string.ok))
        }
    }
}

@Preview(name = "light", showBackground = true)
@Preview(name = "night", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PermissionElementPreview() {
    TagPlayerTheme {
        PermissionElement(
            image = R.drawable.ic_media_video_permission,
            title = R.string.permission_read_media_video,
            description = R.string.permission_read_media_video_description,
        )
    }
}

@Preview(name = "light", showBackground = true)
@Preview(name = "night", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PermissionAlertDialogPreview() {
    TagPlayerTheme {
        PermissionAlertDialog(
            onConfirmClick = {},
        )
    }
}
