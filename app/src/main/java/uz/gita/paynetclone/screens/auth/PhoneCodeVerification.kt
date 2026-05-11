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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.gita.paynetclone.components.OtpInputField
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import uz.gita.paynetclone.R
import uz.gita.paynetclone.components.ResendTimerPanel

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.safeDrawingPadding
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.presenter.auth.AuthViewModel
import uz.gita.paynetclone.presenter.auth.AuthIntent
import uz.gita.paynetclone.presenter.auth.AuthSideEffect
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator

class PhoneCodeVerificationScreen(private val phoneNumber: String) : Screen {
    override val key: ScreenKey = "auth.phone_code.$phoneNumber"

    @Composable
    override fun Content() {
        val viewModel: AuthViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        
        val navigator = LocalAppNavigator.current
        val context = LocalContext.current

        LaunchedEffect(phoneNumber) {
            viewModel.onEvent(AuthIntent.PhoneChanged(phoneNumber))
        }

        LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is AuthSideEffect.NavigateToHome -> {
                        Toast.makeText(context, "Home Screen ochildi", Toast.LENGTH_SHORT).show()
                        navigator.openHome()
                    }
                    is AuthSideEffect.NavigateToPinScreen -> {
                        navigator.openPin(effect.isNewUser)
                    }
                    is AuthSideEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        PhoneCodeVerificationContent(
            phoneNumber = phoneNumber,
            smsCode = state.otp,
            onCodeChange = { 
                viewModel.onEvent(AuthIntent.OtpChanged(it))
                if (it.length == 6) {
                    viewModel.onEvent(AuthIntent.VerifyOtpClicked)
                }
            },
            onBackClick = { navigator.back() },
            isLoading = state.isLoading
        )
    }
}

@Composable
fun formatPhoneMessage(phoneNumber: String): String {
    val formatted = if (phoneNumber.length == 9) {
        "+998 ${phoneNumber.substring(0, 2)} ••• •• ${phoneNumber.substring(7)}"
    } else {
        "+998 $phoneNumber"
    }
    return stringResource(R.string.code_sent_to, formatted)
}

@Composable
fun PhoneCodeVerificationContent(
    phoneNumber: String,
    smsCode: String,
    onCodeChange: (String) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean
) {

    var timerSeconds by remember { mutableStateOf(60) }
    var isResendEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        } else {
            isResendEnabled = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    modifier = Modifier.size(26.dp),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.size(60.dp)
            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            contentAlignment = Alignment.Center){

        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.enter_sms_code),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatPhoneMessage(phoneNumber),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            OtpInputField(
                code = smsCode,
                onCodeChange = onCodeChange
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ResendTimerPanel(
            timerSeconds = timerSeconds,
            isResendEnabled = isResendEnabled,
            onResendClick = {
                timerSeconds = 60
                isResendEnabled = false
            }
        )
    }
}
