package uz.gita.paynetclone.screens.identification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import uz.gita.paynetclone.R
import uz.gita.paynetclone.ui.theme.PaynetGreen

class IdentificationScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isUzbekCitizen by remember { mutableStateOf(true) }
        var documentNumber by remember { mutableStateOf("") }
        var dateOfBirth by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.ident_title),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isUzbekCitizen) PaynetGreen else Color.Transparent)
                            .clickable { isUzbekCitizen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ident_uz_citizen),
                            color = if (isUzbekCitizen) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (isUzbekCitizen) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isUzbekCitizen) PaynetGreen else Color.Transparent)
                            .clickable { isUzbekCitizen = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ident_foreign_citizen),
                            color = if (!isUzbekCitizen) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (!isUzbekCitizen) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1976D2), // Blue tint for info
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.ident_info_msg),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Document Number Field
                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { 
                        val uppercase = it.uppercase()
                        if (uppercase.length <= 14) {
                            documentNumber = uppercase
                        }
                    },
                    label = { Text(stringResource(R.string.ident_doc_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Characters),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = PaynetGreen,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_camera), // Temporary generic icon
                            contentDescription = "QR Scanner",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth Field
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { 
                        if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                            dateOfBirth = it
                        }
                    },
                    label = { Text(stringResource(R.string.ident_dob)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = PaynetGreen,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_my_calendar), // Temporary generic icon
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Next Button
                val isFormValid = documentNumber.isNotEmpty() && dateOfBirth.length == 8
                Button(
                    onClick = {
                        val series = if (documentNumber.length > 2 && documentNumber.take(2).all { it.isLetter() }) documentNumber.substring(0, 2) else "AB"
                        val number = if (documentNumber.length > 2 && documentNumber.take(2).all { it.isLetter() }) documentNumber.substring(2) else documentNumber
                        
                        // Format the date to send: API expects YYYY-MM-DD
                        val formattedDate = if (dateOfBirth.length == 8) {
                            "${dateOfBirth.substring(4,8)}-${dateOfBirth.substring(2,4)}-${dateOfBirth.substring(0,2)}"
                        } else {
                            dateOfBirth
                        }
                        
                        navigator.push(IdentificationSelfieInstructionScreen(series, number, formattedDate))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PaynetGreen,
                        disabledContainerColor = PaynetGreen.copy(alpha = 0.5f),
                        contentColor = Color.White
                    ),
                    enabled = isFormValid
                ) {
                    Text(
                        text = stringResource(R.string.ident_next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

class DateVisualTransformation : androidx.compose.ui.text.input.VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "."
        }

        val offsetMapping = object : androidx.compose.ui.text.input.OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(out), 
            offsetMapping
        )
    }
}
