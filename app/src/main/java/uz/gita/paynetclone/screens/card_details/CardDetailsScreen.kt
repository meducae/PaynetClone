package uz.gita.paynetclone.screens.card_details

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
import uz.gita.paynetclone.components.PaynetCardItem
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
                    CardDetailsContract.SideEffect.NavigateToVerify -> navigator.openIdentification()
                }
            }
        }

        CardDetailsContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CardDetailsContent(
    state: CardDetailsContract.State,
    onEvent: (CardDetailsContract.Intent) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.delete_card_confirm_title),
                    fontFamily = SatoshiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_card_confirm_desc),
                    fontFamily = SatoshiMedium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onEvent(CardDetailsContract.Intent.OnDeleteCardClicked)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontFamily = SatoshiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = SatoshiMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
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
            PaynetCardItem(
                card = state.card ?: Card(
                    id = "1",
                    maskedNumber = "1234",
                    holderName = "Nemo Nemo",
                    expiry = "23/7",
                    balance = 10000,
                    currency = "UZ",
                    isMain = true,
                    isBlocked = false,
                    type = "Paynet"
                ), modifier = Modifier, { cardNumber ->
                    onEvent(CardDetailsContract.Intent.OnCopyClicked)
                })

            Spacer(modifier = Modifier.height(16.dp))

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
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.shield),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column (){
                        Text(
                            text = if (state.isKycVerified) stringResource(R.string.kyc_status_approved_title) else stringResource(R.string.verify_identity),
                            fontFamily = SatoshiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (state.isKycVerified) stringResource(R.string.kyc_status_approved_desc) else stringResource(R.string.verify_identity_desc),
                            fontFamily = SatoshiMedium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!state.isKycVerified) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(CardDetailsContract.Intent.OnVerifyClicked) },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
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
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.arrowback),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                R.string.remaining_transfers,
                                state.remainingTransfers,
                                state.maxTransfers
                            ),
                            fontFamily = SatoshiMedium,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
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
                            color = MaterialTheme.colorScheme.secondary
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
                        color = MaterialTheme.colorScheme.secondary,
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
                        color = MaterialTheme.colorScheme.secondary,
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
                                tint = MaterialTheme.colorScheme.secondary
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
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteDialog = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.delete_card),
                            fontFamily = SatoshiMedium,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.error
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
                tint = MaterialTheme.colorScheme.primary,
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
                tint = MaterialTheme.colorScheme.secondary
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
            tint = MaterialTheme.colorScheme.outline,
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
