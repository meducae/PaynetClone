package uz.gita.paynetclone.screens.card_details

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.R
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.presenter.card_details.CardDetailsContract
import uz.gita.paynetclone.presenter.card_details.CardDetailsViewModel
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator
import uz.gita.paynetclone.ui.theme.PaynetCloneTheme
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium

class CardDetailsScreen(private val cardId: String) : Screen {

    @Composable
    override fun Content() {
        val viewModel: CardDetailsViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        val navigator = LocalAppNavigator.current
        val context = LocalContext.current

        LaunchedEffect(cardId) {
            viewModel.onEvent(CardDetailsContract.Intent.LoadCard(cardId))
        }

        LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { sideEffect ->
                when (sideEffect) {
                    CardDetailsContract.SideEffect.Back -> navigator.back()
                    is CardDetailsContract.SideEffect.ShowToast -> {
                        Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                    }
                    CardDetailsContract.SideEffect.NavigateToPay -> {}
                    CardDetailsContract.SideEffect.NavigateToTopUp -> {}
                    CardDetailsContract.SideEffect.NavigateToTransfer -> {}
                    CardDetailsContract.SideEffect.NavigateToVerify -> {}
                }
            }
        }

        CardDetailsContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun CardDetailsContent(
    state: CardDetailsContract.State,
    onEvent: (CardDetailsContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowback),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onEvent(CardDetailsContract.Intent.OnBackClicked) }
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.paynet_card),
                    fontSize = 20.sp,
                    fontFamily = SatoshiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Card Representation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF00964E), Color(0xFF00C853))
                            )
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.card?.maskedNumber?.chunked(4)?.joinToString(" ") ?: "",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = SatoshiBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.repeat),
                            contentDescription = "Copy",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onEvent(CardDetailsContract.Intent.OnCopyClicked) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verify Identity Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00964E).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulating the "PRO" badge/shield icon
                        Icon(
                            painter = painterResource(id = R.drawable.shield),
                            contentDescription = null,
                            tint = Color(0xFF00964E),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.verify_identity),
                            fontFamily = SatoshiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = stringResource(R.string.verify_identity_desc),
                            fontFamily = SatoshiMedium,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Limits Info Box (Light blue)
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onEvent(CardDetailsContract.Intent.OnVerifyClicked) },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF0F7FF), // Very light blue
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.remove_limits_desc),
                            fontFamily = SatoshiMedium,
                            fontSize = 13.sp,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.arrowback),
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier
                                .size(14.dp)
                                .graphicsLayer(rotationZ = 180f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFD1E4F9), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.remaining_transfers,
                            state.remainingTransfers,
                            state.maxTransfers
                        ),
                        fontFamily = SatoshiMedium,
                        fontSize = 12.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionBtn(
                    icon = R.drawable.add_circle_fill,
                    text = stringResource(R.string.top_up),
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(CardDetailsContract.Intent.OnTopUpClicked) }
                )
                ActionBtn(
                    icon = R.drawable.arrowback, // We'll rotate this for "up-right" look
                    iconRotation = 45f,
                    text = stringResource(R.string.transfer),
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(CardDetailsContract.Intent.OnTransferClicked) }
                )
                ActionBtn(
                    icon = R.drawable.wallet_3_line,
                    text = stringResource(R.string.pay),
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(CardDetailsContract.Intent.OnPayClicked) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Cashout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onEvent(CardDetailsContract.Intent.OnQrCashoutClicked) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.qr_cashout),
                            fontFamily = SatoshiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = stringResource(R.string.qr_cashout_desc),
                            fontFamily = SatoshiMedium,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.payment_history),
                        fontFamily = SatoshiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(R.string.no_payments_yet),
                        fontFamily = SatoshiMedium,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Links (Oferta, Shartlar)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column {
                    SettingsRow(
                        icon = R.drawable.malumotnoma,
                        text = stringResource(R.string.offer),
                        onClick = { onEvent(CardDetailsContract.Intent.OnOfferClicked) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    SettingsRow(
                        icon = R.drawable.info,
                        text = stringResource(R.string.terms_and_limits),
                        onClick = { onEvent(CardDetailsContract.Intent.OnTermsClicked) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Settings (Main Card)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.settings),
                        fontFamily = SatoshiBold,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.main_card),
                                fontFamily = SatoshiMedium,
                                fontSize = 15.sp
                            )
                        }
                        Switch(
                            checked = state.isMainCard,
                            onCheckedChange = { onEvent(CardDetailsContract.Intent.OnMainCardChanged(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00964E),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActionBtn(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    iconRotation: Float = 0f,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF00964E),
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(rotationZ = iconRotation)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontFamily = SatoshiMedium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SettingsRow(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = SatoshiMedium,
                fontSize = 15.sp
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.arrowback),
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(rotationZ = 180f)
        )
    }
}

@Preview
@Composable
fun CardDetailsPreview() {
    PaynetCloneTheme {
        CardDetailsContent(
            state = CardDetailsContract.State(
                card = Card(
                    id = "1",
                    maskedNumber = "7777013723143540",
                    holderName = "Owner",
                    expiry = "12/25",
                    balance = 0,
                    currency = "so'm",
                    isMain = true,
                    isBlocked = false,
                    type = "PAYNET"
                ),
                isMainCard = true,
                remainingTransfers = 1,
                maxTransfers = 1
            ),
            onEvent = {}
        )
    }
}
