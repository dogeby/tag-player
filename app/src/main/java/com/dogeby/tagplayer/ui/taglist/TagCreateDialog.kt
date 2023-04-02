package com.dogeby.tagplayer.ui.taglist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagCreateDialog(
    isError: () -> Boolean,
    supportingText: () -> String,
    onCreateButtonClick: (String) -> Unit,
    onCancelButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    AlertDialog(
        onDismissRequest = onCancelButtonClick,
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.tagCreateDialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dialog_paddingBetweenTitleAndBody)))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    supportingText = { Text(text = supportingText()) },
                    isError = isError(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dialog_paddingBetweenBodyAndAction)))

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(
                        onClick = onCancelButtonClick,
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    TextButton(
                        onClick = { onCreateButtonClick(name.text) },
                    ) {
                        Text(stringResource(id = R.string.create))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagCreateDialogPreview() {
    TagPlayerTheme {
        TagCreateDialog(
            isError = { false },
            supportingText = { "" },
            onCreateButtonClick = {},
            onCancelButtonClick = {},
        )
    }
}
