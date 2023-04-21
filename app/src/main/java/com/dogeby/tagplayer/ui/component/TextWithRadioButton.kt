package com.dogeby.tagplayer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun TextWithRadioButton(
    text: String,
    selected: () -> Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRowClickable: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rowModifier = if (isRowClickable) {
        modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
    } else {
        modifier
    }
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text)
        RadioButton(
            selected = selected(),
            onClick = onClick,
            interactionSource = interactionSource,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextWithRadioButtonPreview() {
    TagPlayerTheme {
        TextWithRadioButton(
            text = "내용",
            selected = { true },
            onClick = {},
            modifier = Modifier.width(100.dp)
        )
    }
}
