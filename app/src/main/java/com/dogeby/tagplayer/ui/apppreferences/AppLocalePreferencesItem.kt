package com.dogeby.tagplayer.ui.apppreferences

import androidx.appcompat.app.AppCompatDelegate
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
import androidx.core.os.LocaleListCompat
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.component.TextWithRadioButton

@Composable
fun AppLocalePreferencesItem(
    modifier: Modifier = Modifier,
) {
    var isAppLanguageSettingDialogShown by rememberSaveable {
        mutableStateOf(false)
    }

    if (isAppLanguageSettingDialogShown) {
        AppLocaleSettingDialog(onDismissRequest = { isAppLanguageSettingDialogShown = false })
    }

    PreferencesItem(
        title = stringResource(id = R.string.appPreferences_locale_title),
        body = if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            stringResource(id = R.string.appPreferences_locale_body_systemSetting)
        } else {
            stringResource(id = R.string.appPreferences_locale_language)
        },
        onItemClick = { isAppLanguageSettingDialogShown = true },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLocaleSettingDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val localeOptions = mapOf(
        R.string.appPreferences_locale_body_systemSetting to "",
        R.string.appPreferences_locale_body_english to "en",
        R.string.appPreferences_locale_body_korean to "ko",
    ).mapKeys { stringResource(id = it.key) }

    val appLanguageCode = if (AppCompatDelegate.getApplicationLocales().isEmpty) {
        ""
    } else {
        stringResource(id = R.string.appPreferences_locale_language_code)
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
                    .padding(dimensionResource(id = R.dimen.padding_large)),
            ) {
                Text(
                    text = stringResource(id = R.string.appPreferences_locale_title),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                localeOptions.forEach { localeOption ->
                    TextWithRadioButton(
                        text = localeOption.key,
                        selected = { appLanguageCode == localeOption.value },
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(
                                    localeOption.value
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isRowClickable = true,
                    )
                }
            }
        }
    }
}
