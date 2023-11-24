package com.dogeby.tagplayer.ui.apppreferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import com.dogeby.tagplayer.ui.component.TextWithRadioButton
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun AppThemeModePreferencesItem(
    currentAppThemeMode: () -> AppThemeMode,
    onSetAppThemeMode: (appThemeMode: AppThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAppThemeSettingDialogShown by rememberSaveable {
        mutableStateOf(false)
    }

    if (isAppThemeSettingDialogShown) {
        AppThemeModeSettingDialog(
            currentAppThemeMode = currentAppThemeMode,
            onDismissRequest = { isAppThemeSettingDialogShown = false },
            onSetAppThemeMode = onSetAppThemeMode,
        )
    }

    ClickablePreferencesItem(
        title = stringResource(id = R.string.appPreferences_theme_title),
        body = when (currentAppThemeMode()) {
            AppThemeMode.SYSTEM_SETTING -> {
                stringResource(id = R.string.appPreferences_theme_body_systemSetting)
            }
            AppThemeMode.LIGHT -> {
                stringResource(id = R.string.appPreferences_theme_body_light)
            }
            AppThemeMode.DARK -> {
                stringResource(id = R.string.appPreferences_theme_body_dark)
            }
        },
        onItemClick = { isAppThemeSettingDialogShown = true },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppThemeModeSettingDialog(
    currentAppThemeMode: () -> AppThemeMode,
    onDismissRequest: () -> Unit,
    onSetAppThemeMode: (appThemeMode: AppThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    .padding(dimensionResource(id = R.dimen.padding_large)),
            ) {
                Text(
                    text = stringResource(id = R.string.appPreferences_theme_title),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                TextWithRadioButton(
                    text = stringResource(id = R.string.appPreferences_theme_body_systemSetting),
                    selected = { currentAppThemeMode() == AppThemeMode.SYSTEM_SETTING },
                    onClick = { onSetAppThemeMode(AppThemeMode.SYSTEM_SETTING) },
                    modifier = Modifier.fillMaxWidth(),
                    isRowClickable = true,
                )
                TextWithRadioButton(
                    text = stringResource(id = R.string.appPreferences_theme_body_light),
                    selected = { currentAppThemeMode() == AppThemeMode.LIGHT },
                    onClick = { onSetAppThemeMode(AppThemeMode.LIGHT) },
                    modifier = Modifier.fillMaxWidth(),
                    isRowClickable = true,
                )
                TextWithRadioButton(
                    text = stringResource(id = R.string.appPreferences_theme_body_dark),
                    selected = { currentAppThemeMode() == AppThemeMode.DARK },
                    onClick = { onSetAppThemeMode(AppThemeMode.DARK) },
                    modifier = Modifier.fillMaxWidth(),
                    isRowClickable = true,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppThemeModeSettingDialogPreview() {
    TagPlayerTheme {
        AppThemeModeSettingDialog(
            currentAppThemeMode = { AppThemeMode.SYSTEM_SETTING },
            onDismissRequest = {},
            onSetAppThemeMode = {},
        )
    }
}
