package uz.gita.paynetclone.screens.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.R
import uz.gita.paynetclone.presenter.auth.PinIntent
import uz.gita.paynetclone.presenter.auth.PinMode
import uz.gita.paynetclone.presenter.auth.PinSideEffect
import uz.gita.paynetclone.presenter.auth.PinViewModel
import uz.gita.paynetclone.screens.SatoshiBold
import uz.gita.paynetclone.screens.SatoshiMedium

class PinScreen(private val isNewUser: Boolean) : Screen {

    @Composable
    override fun Content() {
        val viewModel: PinViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.setInitialMode(isNewUser)
        }

        LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    PinSideEffect.NavigateToHome -> {
                        navigator.replaceAll(uz.gita.paynetclone.feature.home.presentation.HomeScreen())
                    }
                    is PinSideEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        PinScreenContent(
            state = state,
            onIntent = viewModel::onEvent
        )
    }
}

@Composable
fun PinScreenContent(
    state: uz.gita.paynetclone.presenter.auth.PinState,
    onIntent: (PinIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF86EFAC), Color(0xFF22C55E))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(36.dp))
            } else {
                Icon(
                    painter = painterResource(R.drawable.lockicon),
                    contentDescription = "Lock",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(targetState = state.mode, label = "PinTitle") { mode ->
            val titleRes = when (mode) {
                PinMode.CREATE -> R.string.create_pin
                PinMode.CONFIRM -> R.string.confirm_pin
                PinMode.ENTER -> R.string.enter_pin
            }
            Text(
                text = stringResource(titleRes),
                fontSize = 26.sp,
                fontFamily = SatoshiBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        if (state.error != null) {
            Text(
                text = state.error!!,
                fontSize = 14.sp,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.security_pin_desc),
                fontSize = 15.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(64.dp))
        
        // PIN Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 4) {
                val isFilled = i < state.pin.length
                val dotColor = if (isFilled) Color(0xFF22C55E) else Color(0xFFF1F5F9)
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = dotColor, shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Numpad(
            onNumberClick = { onIntent(PinIntent.OnNumberEntered(it)) },
            onDeleteClick = { onIntent(PinIntent.OnDeleteClicked) },
            onForgotClick = { onIntent(PinIntent.OnForgotClicked) },
            onBiometricClick = { onIntent(PinIntent.OnBiometricClicked) },
            showBiometric = state.mode == PinMode.ENTER
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun Numpad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onForgotClick: () -> Unit,
    onBiometricClick: () -> Unit,
    showBiometric: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { number ->
                    PinNumberButton(number = number, onClick = { onNumberClick(number) })
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .clickable { onForgotClick() }
                .padding(8.dp),
                contentAlignment = Alignment.Center){
                Text(
                    text = stringResource(R.string.forgot_pin),
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                PinNumberButton(number = "0", onClick = { onNumberClick("0") })
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .clickable { 
                        if (showBiometric) onBiometricClick() else onDeleteClick()
                    }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showBiometric) {
                    Icon(
                        painter = painterResource(R.drawable.face),
                        contentDescription = "Face ID",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.back), // Assuming back is backspace
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PinNumberButton(number: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 32.sp,
            fontFamily = SatoshiMedium,
            color = Color.Black
        )
    }
}
