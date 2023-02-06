package com.dogeby.tagplayer.ui.permission

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.BuildConfig
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun PermissionAlertDialog(
    modifier: Modifier = Modifier,
    dismissButtonText: String = stringResource(id = R.string.close),
    isDismissRequest: Boolean = false,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier.padding(all = dimensionResource(id = R.dimen.padding_medium)),
        onDismissRequest = { if (isDismissRequest) onDismiss() },
        text = {
            Text(
                text = "${stringResource(id = R.string.permission_dialog_require_description)}\n\n${
                stringResource(id = R.string.permission_dialog_method_description)
                }",
            )
        },
        confirmButton = {
            val context = LocalContext.current
            Button(
                onClick = { startTagPlayerAppDetailsSettings(context) },
            ) {
                Text(text = stringResource(id = R.string.permission_dialog_app_setting))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButtonText)
            }
        },
    )
}

private fun startTagPlayerAppDetailsSettings(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID),
        ),
    )
}

@Preview(name = "light", showBackground = true)
@Preview(name = "night", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PermissionAlertDialogPreview() {
    TagPlayerTheme {
        PermissionAlertDialog()
    }
}
