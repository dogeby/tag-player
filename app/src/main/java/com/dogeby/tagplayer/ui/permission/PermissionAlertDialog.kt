package com.dogeby.tagplayer.ui.permission

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
    requireDescription: String,
    methodDescription: String,
    activityStartAction: String,
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
                text = "$requireDescription\n\n$methodDescription",
            )
        },
        confirmButton = {
            val context = LocalContext.current
            Button(
                onClick = { startTagPlayerAppDetailsSettings(activityStartAction, context) },
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

private fun startTagPlayerAppDetailsSettings(
    activityStartAction: String,
    context: Context
) {
    context.startActivity(
        Intent(
            activityStartAction,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID),
        ),
    )
}

@Preview(name = "light", showBackground = true)
@Preview(name = "night", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PermissionAlertDialogPreview() {
    TagPlayerTheme {
        PermissionAlertDialog("test", "test", "")
    }
}
