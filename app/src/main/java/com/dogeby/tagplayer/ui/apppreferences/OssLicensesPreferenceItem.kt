package com.dogeby.tagplayer.ui.apppreferences

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dogeby.tagplayer.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@Composable
fun OssLicensesPreferenceItem(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val title = stringResource(id = R.string.appPreferences_ossLicenses_title)

    ClickablePreferencesItem(
        title = title,
        body = stringResource(id = R.string.appPreferences_ossLicenses_body),
        onItemClick = {
            OssLicensesMenuActivity.setActivityTitle(title)
            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
        },
        modifier = modifier,
    )
}
