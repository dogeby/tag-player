package com.dogeby.tagplayer.ui.apppreferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.permission.WriteSettingsPermissionCheck
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun AutoRotationPreferencesItem(
    currentAutoRotationValue: () -> Boolean,
    onSetAutoRotation: (isAutoRotation: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isCheckWriteSettingsPermission by rememberSaveable { mutableStateOf(false) }

    if (isCheckWriteSettingsPermission) {
        WriteSettingsPermissionCheck(
            onDismiss = { isCheckWriteSettingsPermission = false },
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PreferencesItem(
            title = stringResource(id = R.string.appPreferences_autoRotation_title),
            body = stringResource(id = R.string.appPreferences_autoRotation_body),
            modifier = Modifier.weight(1f, false),
        )
        Switch(
            checked = currentAutoRotationValue(),
            onCheckedChange = {
                isCheckWriteSettingsPermission = it
                onSetAutoRotation(it)
            },
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AutoRotationPreferencesItemPreview() {
    TagPlayerTheme {
        AutoRotationPreferencesItem(
            currentAutoRotationValue = { false },
            onSetAutoRotation = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
