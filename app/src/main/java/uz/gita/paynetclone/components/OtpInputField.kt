package uz.gita.paynetclone.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OtpInputField(
    code: String,
    onCodeChange: (String) -> Unit,
    length: Int = 6,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = code,
        onValueChange = {
            if (it.length <= length && it.all { char -> char.isDigit() }) {
                onCodeChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        modifier = Modifier.padding(vertical = 16.dp),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.alpha(0f)) {
                    innerTextField()
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(length) { index ->
                        val char = if (index < code.length) code[index].toString() else ""
                        val isFocused = index == code.length
                        val isCursorVisible = index == code.length

                        OtpInputCell(
                            char = char,
                            isFocused = isFocused,
                            isCursorVisible = isCursorVisible
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun OtpInputCell(
    char: String,
    isFocused: Boolean,
    isCursorVisible: Boolean
) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(55.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCursorVisible && char.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Text(
            text = char,
            style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            textAlign = TextAlign.Center
        )
    }
}
