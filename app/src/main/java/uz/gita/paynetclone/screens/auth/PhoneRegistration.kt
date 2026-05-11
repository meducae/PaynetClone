@file:Suppress("DEPRECATION")

package uz.gita.paynetclone.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.Image
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.presenter.auth.AuthViewModel
import uz.gita.paynetclone.presenter.auth.AuthIntent
import uz.gita.paynetclone.presenter.auth.AuthSideEffect
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium

class PhoneRegistrationScreen : Screen {
    override val key: ScreenKey = "auth.phone_registration"

    @Composable
    override fun Content() {
        val viewModel: AuthViewModel = getViewModel()
        val state by viewModel.state.collectAsState()

        val navigator = LocalAppNavigator.current
        val context = LocalContext.current

        LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is AuthSideEffect.NavigateToOtpScreen -> {
                        navigator.openOtpVerification(state.phone)
                    }

                    is AuthSideEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        PhoneRegistrationContent(
            phoneNumber = state.phone,
            onPhoneNumberChange = { viewModel.onEvent(AuthIntent.PhoneChanged(it)) },
            onContinueClicked = { viewModel.onEvent(AuthIntent.SendOtpClicked) },
            isLoading = state.isLoading
        )
    }
}

@Composable
fun PhoneRegistrationContent(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onContinueClicked: () -> Unit,
    isLoading: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            LanguageSelector()
        }
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primary
                        ),
                        center = androidx.compose.ui.geometry.Offset(30f, 30f),
                        radius = 100f
                    )
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.enter_phone_number),
            fontSize = 24.sp,
            fontFamily = SatoshiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.to_become_client),
            fontSize = 14.sp,
            fontFamily = SatoshiMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        PhoneInputTextField(
            phoneNumber = phoneNumber,
            onPhoneNumberChange = { onPhoneNumberChange(it) }
        )
        Spacer(modifier = Modifier.weight(1f))

        val isButtonEnabled = phoneNumber.length == 9

        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Button(
                onClick = onContinueClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.continue_btn),
                    fontSize = 16.sp,
                    fontFamily = SatoshiBold,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TermsAndConditionsText()
    }
}

@Composable
fun LanguageSelector() {
    val currentLang by PrefsManager.language.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val languages = listOf(
        Pair("uz", stringResource(R.string.language_uz)),
        Pair("ru", stringResource(R.string.language_ru)),
        Pair("en", stringResource(R.string.language_en))
    )

    val currentLangName = languages.find { it.first == currentLang }?.second ?: stringResource(R.string.language_uz)

    Box {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(20.dp))
                .clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = "文A", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentLangName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.down),
                    contentDescription = "DropDown",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontFamily = SatoshiMedium,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        PrefsManager.setLanguage(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PhoneInputTextField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.flaguzb), contentDescription = "uzbekistan flag(ozim yozdim)", modifier = Modifier.size(28.dp))
        Icon(
            painter = painterResource(R.drawable.down),
            contentDescription = "Country code",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "+ 998",
            fontSize = 18.sp,
            fontFamily = SatoshiMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = phoneNumber,
            onValueChange = { newValue ->
                if (newValue.length <= 9 && newValue.all { it.isDigit() }) {
                    onPhoneNumberChange(newValue)
                }
            },
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontFamily = SatoshiMedium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = UzPhoneVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

class UzPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 9) text.text.substring(0..8) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 4 || i == 6) out += " "
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                if (offset <= 6) return offset + 2
                if (offset <= 9) return offset + 3
                return 12
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 6) return offset - 1
                if (offset <= 9) return offset - 2
                if (offset <= 12) return offset - 3
                return 9
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@Composable
fun TermsAndConditionsText() {
    val textPart1 = stringResource(R.string.by_clicking_continue_i_agree)
    val termsLink = stringResource(R.string.terms_of_service)
    val textPart2 = stringResource(R.string.and_consent_to_data_processing)

    val linkColor = MaterialTheme.colorScheme.primary

    val annotatedString = buildAnnotatedString {
        append(textPart1)

        pushStringAnnotation(tag = "TERMS", annotation = "terms_url")
        withStyle(style = SpanStyle(color = linkColor)) {
            append(termsLink)
        }
        pop()

        append(textPart2)
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontFamily = SatoshiMedium,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let {

                }
        }
    )
}
