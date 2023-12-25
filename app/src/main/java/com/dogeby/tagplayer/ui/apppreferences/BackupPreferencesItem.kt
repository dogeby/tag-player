package com.dogeby.tagplayer.ui.apppreferences

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.ConfigurationCompat
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupPreferencesItem(
    encodedTagsUiState: EncodeTagsUiState,
    onLoadTags: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isBackupDialogShown by rememberSaveable {
        mutableStateOf(false)
    }
    ClickablePreferencesItem(
        title = stringResource(id = R.string.appPreferences_backup_title),
        body = stringResource(id = R.string.appPreferences_backup_body),
        onItemClick = { isBackupDialogShown = true },
        modifier = modifier,
    )

    if (isBackupDialogShown) {
        BackupDialog(
            encodedTagsUiState = encodedTagsUiState,
            onLoadTags = onLoadTags,
            onDismissRequest = { isBackupDialogShown = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupDialog(
    encodedTagsUiState: EncodeTagsUiState,
    onLoadTags: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var createTagBackupFile by rememberSaveable {
        mutableStateOf(false)
    }
    var loadBackupTagsJson by rememberSaveable {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(dimensionResource(id = R.dimen.padding_large))
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = stringResource(id = R.string.appPreferences_backup_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                Text(
                    text = stringResource(id = R.string.appPreferences_backup_dialog_body),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Row {
                        TextButton(onClick = { loadBackupTagsJson = true }) {
                            Text(text = stringResource(id = R.string.load))
                        }
                        TextButton(onClick = { createTagBackupFile = true }) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                }
            }
        }
    }

    if (createTagBackupFile) {
        if (encodedTagsUiState is EncodeTagsUiState.Success) {
            SaveTagBackupFile(
                backupTagsJson = encodedTagsUiState.backupTagsJson,
                onFinish = { isSuccess ->
                    createTagBackupFile = false
                    Toast.makeText(
                        context,
                        if (isSuccess) R.string.appPreferences_backup_save_tags_success else R.string.appPreferences_backup_save_tags_failure,
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }
    if (loadBackupTagsJson) {
        LoadBackupTagsJson(
            onFinish = { backupTagsJson ->
                loadBackupTagsJson = false
                if (backupTagsJson != null) {
                    onLoadTags(backupTagsJson)
                    return@LoadBackupTagsJson
                }
                Toast.makeText(context, R.string.appPreferences_backup_load_tags_failure, Toast.LENGTH_SHORT).show()
            },
        )
    }
}

@Composable
fun SaveTagBackupFile(
    backupTagsJson: String,
    onFinish: (isSuccess: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
    val launcher = rememberLauncherForActivityResult(
        contract = object : CreateDocument("application/json") {
            override fun createIntent(context: Context, input: String): Intent {
                return super.createIntent(context, input).putExtra(Intent.EXTRA_TITLE, "TagPlayer_tag_backup_${SimpleDateFormat("yy:MM:dd:hh:mm:ss", locale).format(Date())}")
            }
        }
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openOutputStream(uri).use {
                val writer = it?.bufferedWriter()
                writer?.write(backupTagsJson)
                writer?.flush()
            }
            onFinish(true)
            return@rememberLauncherForActivityResult
        }
        onFinish(false)
    }
    LaunchedEffect(launcher) {
        launcher.launch("application/json")
    }
}

@Composable
fun LoadBackupTagsJson(
    onFinish: (String?) -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri).use {
                val reader = it?.bufferedReader()
                val json = reader?.readText()
                onFinish(json)
            }
            return@rememberLauncherForActivityResult
        }
        onFinish(null)
    }
    LaunchedEffect(launcher) {
        launcher.launch(arrayOf("application/json"))
    }
}

@Preview
@Composable
fun BackupDialogPreview() {
    TagPlayerTheme {
        BackupDialog(
            encodedTagsUiState = EncodeTagsUiState.Success(""),
            onLoadTags = {},
            onDismissRequest = {},
        )
    }
}
