package com.dogeby.tagplayer.ui.apppreferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun PreferencesItem(
    title: String,
    body: String,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleTextColor: Color = Color.Unspecified,
    bodyTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onItemClick),
    ) {
        Text(
            text = title,
            color = titleTextColor,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Text(
            text = body,
            color = bodyTextColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesItemPreview() {
    TagPlayerTheme {
        PreferencesItem(
            title = "설정 제목",
            body = "설명",
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesItemsPreview() {
    TagPlayerTheme {
        Column {
            repeat(5) {
                PreferencesItem(
                    title = "설정 제목 $it",
                    body = "설명 $it",
                    onItemClick = {}
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            }
        }
    }
}
