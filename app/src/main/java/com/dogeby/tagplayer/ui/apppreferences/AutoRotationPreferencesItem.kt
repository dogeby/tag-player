package com.dogeby.tagplayer.ui.apppreferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun AutoRotationPreferencesItem(
    currentAutoRotationValue: () -> Boolean,
    onSetAutoRotation: (isAutoRotation: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            onCheckedChange = onSetAutoRotation,
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
