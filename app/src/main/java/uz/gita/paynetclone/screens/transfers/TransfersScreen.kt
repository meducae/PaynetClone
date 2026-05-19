package uz.gita.paynetclone.screens.transfers

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.R
import uz.gita.paynetclone.components.PaynetBottomNavigation
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.entity.transfer.RecipientCard
import uz.gita.paynetclone.presenter.transfers.TransfersContract
import uz.gita.paynetclone.presenter.transfers.TransfersViewModel
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium

class TransfersScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: TransfersViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is TransfersContract.SideEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> { /* Handle other side effects */ }
                }
            }
        }

        TransfersContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            if (state.transferStep == TransfersContract.TransferStep.SEARCH && !state.isSearching) {
                PaynetBottomNavigation(selectedIndex = 1)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when (state.transferStep) {
                TransfersContract.TransferStep.SEARCH -> {
                    SearchStepContent(state, onEvent)
                }
                TransfersContract.TransferStep.DETAILS -> {
                    TransferDetailsContent(state, onEvent)
                }
                TransfersContract.TransferStep.CONFIRMATION -> {
                    TransferConfirmationContent(state, onEvent)
                }
                TransfersContract.TransferStep.CONFIRM_PIN -> {
                    ConfirmPinStepContent(state, onEvent)
                }
                TransfersContract.TransferStep.CONFIRM_OTP -> {
                    ConfirmOtpStepContent(state, onEvent)
                }
                TransfersContract.TransferStep.SUCCESS -> {
                    TransferSuccessContent(state, onEvent)
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00B159))
                }
            }
            
            val context = LocalContext.current
            state.errorMessage?.let { message ->
                LaunchedEffect(message) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            if (state.isCardSelectionVisible) {
                ModalBottomSheet(
                    onDismissRequest = { onEvent(TransfersContract.Intent.DismissCardSelection) },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    CardSelectionBottomSheetContent(
                        cards = state.myCards,
                        selectedCard = state.selectedFromCard,
                        onCardSelected = { onEvent(TransfersContract.Intent.OnFromCardSelected(it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchStepContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.nav_transfers),
                fontSize = 28.sp,
                fontFamily = SatoshiBold,
                color = Color.Black
            )

            if (state.isSearching) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontSize = 16.sp,
                    fontFamily = SatoshiMedium,
                    color = Color.Black,
                    modifier = Modifier.clickable { onEvent(TransfersContract.Intent.OnCancelSearchClicked) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF1F3F5),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (state.searchText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontFamily = SatoshiMedium
                        )
                    }
                    BasicTextField(
                        value = state.searchText,
                        onValueChange = { 
                            val digitsOnly = it.filter { char -> char.isDigit() }.take(16)
                            onEvent(TransfersContract.Intent.SearchChanged(digitsOnly)) 
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = SatoshiMedium,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused && !state.isSearching) {
                                    onEvent(TransfersContract.Intent.OnSearchClicked)
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = uz.gita.paynetclone.screens.home.CardNumberVisualTransformation(),
                        singleLine = true
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.contact_book),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEvent(TransfersContract.Intent.OnContactClicked) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.qr_code),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEvent(TransfersContract.Intent.OnQrClicked) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isSearching) {
            SearchingView(state, onEvent)
        } else {
            DashboardView(state, onEvent)
        }
    }
}

@Composable
fun DashboardView(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEvent(TransfersContract.Intent.OnUzumBannerClicked) },
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.uzum_tbc_banner_text),
                    fontFamily = SatoshiBold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF00B159), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer(rotationZ = 270f)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE9ECEF)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            TabItem(
                text = stringResource(R.string.recent),
                isSelected = state.selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { onEvent(TransfersContract.Intent.TabSelected(0)) }
            )
            TabItem(
                text = stringResource(R.string.by_template),
                isSelected = state.selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { onEvent(TransfersContract.Intent.TabSelected(1)) }
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    EmptyTransfersState()

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.transfer_header),
        fontSize = 24.sp,
        fontFamily = SatoshiBold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        TransferOptionItem(
            title = stringResource(R.string.to_my_cards),
            desc = stringResource(R.string.to_my_cards_desc),
            icons = listOf(R.drawable.down, R.drawable.refresh),
            modifier = Modifier.weight(1f),
            onClick = { onEvent(TransfersContract.Intent.OnMyCardsClicked) }
        )
        Spacer(modifier = Modifier.width(12.dp))
        TransferOptionItem(
            title = stringResource(R.string.to_atto),
            desc = stringResource(R.string.to_atto_desc),
            icons = listOf(R.drawable.refresh),
            modifier = Modifier.weight(1f),
            onClick = { onEvent(TransfersContract.Intent.OnAttoClicked) }
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun SearchingView(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE9ECEF)
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                TabItem(
                    text = stringResource(R.string.recent),
                    isSelected = state.selectedTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(TransfersContract.Intent.TabSelected(0)) }
                )
                TabItem(
                    text = stringResource(R.string.my_cards),
                    isSelected = state.selectedTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(TransfersContract.Intent.TabSelected(1)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.searchedRecipient?.let { recipient ->
            RecipientCardItem(
                recipient = recipient,
                onClick = { onEvent(TransfersContract.Intent.OnRecipientSelected(recipient)) }
            )
        } ?: if (state.selectedTab == 0) {
            if (state.recentCards.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.search_no_history), color = Color.Gray, fontFamily = SatoshiMedium)
                }
            } else {
                state.recentCards.forEach { recipient ->
                    RecipientCardItem(recipient = recipient, onClick = { onEvent(TransfersContract.Intent.OnRecipientSelected(recipient)) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            state.myCards.forEach { card ->
                MyCardMiniItem(card = card, onClick = { 
                    onEvent(TransfersContract.Intent.OnRecipientSelected(RecipientCard(card.maskedNumber, card.holderName)))
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TransferDetailsContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onEvent(TransfersContract.Intent.OnBackClicked) }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.transfer_to_card_title), fontSize = 20.sp, fontFamily = SatoshiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.selectedFromCard?.let { card ->
            SourceCardItem(card = card, onClick = { onEvent(TransfersContract.Intent.OpenCardSelection) })
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF1F3F5), CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }

        state.selectedToCard?.let { recipient ->
            TargetCardItem(recipient = recipient)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF1F3F5)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.you_are_transferring), fontSize = 12.sp, color = Color.Gray, fontFamily = SatoshiMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = state.amount,
                        onValueChange = { onEvent(TransfersContract.Intent.AmountChanged(it)) },
                        textStyle = TextStyle(fontSize = 24.sp, fontFamily = SatoshiBold, color = Color.Black),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Text(text = stringResource(R.string.som), fontSize = 24.sp, fontFamily = SatoshiBold)
                }
            }
        }

        Text(
            text = "${stringResource(R.string.commission)} 0.0%",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick amounts
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("50 000", "100 000", "200 000", "500 000").forEach { amt ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    color = Color.White,
                    modifier = Modifier.clickable { onEvent(TransfersContract.Intent.AmountChanged(amt.replace(" ", ""))) }
                ) {
                    Text(text = amt, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.Transparent
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.add_greeting_card), fontFamily = SatoshiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(24.dp).background(Color(0xFF00B159), CircleShape), contentAlignment = Alignment.Center) {
                        Text(text = "+", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (state.amount.isNotEmpty()) {
            Button(
                onClick = { onEvent(TransfersContract.Intent.OnContinueClicked) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B159))
            ) {
                Text(text = stringResource(R.string.continue_btn), fontFamily = SatoshiBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TransferConfirmationContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onEvent(TransfersContract.Intent.OnBackClicked) }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.confirm_transfer), fontSize = 20.sp, fontFamily = SatoshiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        state.selectedFromCard?.let { card ->
            SourceCardItem(card = card, onClick = {})
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).offset(y = (-8).dp),
                    tint = Color.Gray
                )
            }
        }

        state.selectedToCard?.let { recipient ->
            TargetCardItem(recipient = recipient)
        }

        Spacer(modifier = Modifier.height(32.dp))

        DetailItem(label = stringResource(R.string.you_are_transferring), value = "${state.amount} ${stringResource(R.string.som)}")
        DetailItem(label = stringResource(R.string.commission), value = "0 ${stringResource(R.string.som)}")
        DetailItem(label = stringResource(R.string.will_be_debited), value = "${state.amount} ${stringResource(R.string.som)}")

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.commission_percent, "0.0"),
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { onEvent(TransfersContract.Intent.OnConfirmTransferClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B159))
        ) {
            Text(text = stringResource(R.string.transfer), fontFamily = SatoshiBold, fontSize = 16.sp)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, fontFamily = SatoshiMedium)
        Text(text = value, color = Color.Black, fontSize = 18.sp, fontFamily = SatoshiBold)
    }
}

@Composable
fun CardSelectionBottomSheetContent(
    cards: List<Card>,
    selectedCard: Card?,
    onCardSelected: (Card) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.select_card),
            fontFamily = SatoshiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        cards.forEach { card ->
            val isSelected = card.id == selectedCard?.id
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCardSelected(card) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFF1F3F5) else Color.Transparent,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00B159)) else null
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF006B3E), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.refresh),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AgroBank • ${card.maskedNumber.takeLast(4)}",
                            fontFamily = SatoshiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${card.balance} so'm",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF00B159),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourceCardItem(card: Card, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF006B3E), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.refresh), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "AgroBank • ${card.maskedNumber.takeLast(4)}", fontFamily = SatoshiBold, fontSize = 16.sp)
                Text(text = "${card.balance} so'm", color = Color.Gray, fontSize = 14.sp)
            }
            Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 270f), tint = Color.Gray)
        }
    }
}

@Composable
fun TargetCardItem(recipient: RecipientCard) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF0096C7), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.refresh), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = recipient.ownerName, fontFamily = SatoshiBold, fontSize = 16.sp)
                Text(text = recipient.cardNumber, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 270f), tint = Color.Gray)
        }
    }
}

@Composable
fun RecipientCardItem(recipient: RecipientCard, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF0096C7), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.refresh), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = recipient.cardNumber, fontFamily = SatoshiBold, fontSize = 16.sp)
                Text(text = recipient.ownerName, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 270f), tint = Color.Gray)
        }
    }
}

@Composable
fun MyCardMiniItem(card: Card, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF006B3E), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.refresh), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = card.maskedNumber, fontFamily = SatoshiBold, fontSize = 16.sp)
                Text(text = card.holderName, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(painter = painterResource(id = R.drawable.down), contentDescription = null, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 270f), tint = Color.Gray)
        }
    }
}

@Composable
fun EmptyTransfersState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF1F3F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.repeat),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.empty_transfers_title),
                fontFamily = SatoshiBold,
                fontSize = 16.sp
            )
            Text(
                text = stringResource(R.string.empty_transfers_desc),
                fontFamily = SatoshiMedium,
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ConfirmPinStepContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(TransfersContract.Intent.OnBackClicked) }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = null)
            }
            Text(
                text = stringResource(R.string.confirm_pin),
                fontSize = 20.sp,
                fontFamily = SatoshiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.enter_pin),
            fontSize = 24.sp,
            fontFamily = SatoshiBold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.security_pin_desc),
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Simple PIN dots
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (index < pin.length) Color(0xFF00B159) else Color(0xFFE9ECEF),
                            CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Custom Number Pad
        NumberPad(
            onNumberClick = { num ->
                if (pin.length < 4) {
                    pin += num
                    if (pin.length == 4) {
                        onEvent(TransfersContract.Intent.OnPinEntered(pin))
                    }
                }
            },
            onDeleteClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ConfirmOtpStepContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    var otp by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(TransfersContract.Intent.OnBackClicked) }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = null)
            }
            Text(
                text = stringResource(R.string.sms_confirmation),
                fontSize = 20.sp,
                fontFamily = SatoshiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.enter_sms_code),
            fontSize = 24.sp,
            fontFamily = SatoshiBold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Simple OTP display
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(6) { index ->
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F3F5),
                    border = if (index == otp.length) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00B159)) else null
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (index < otp.length) otp[index].toString() else "",
                            fontSize = 20.sp,
                            fontFamily = SatoshiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NumberPad(
            onNumberClick = { num ->
                if (otp.length < 6) {
                    otp += num
                    if (otp.length == 6) {
                        onEvent(TransfersContract.Intent.OnOtpEntered(otp))
                    }
                }
            },
            onDeleteClick = {
                if (otp.isNotEmpty()) {
                    otp = otp.dropLast(1)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "DEL")
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        numbers.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clickable(enabled = item.isNotEmpty()) {
                                if (item == "DEL") onDeleteClick() else onNumberClick(item)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (item == "DEL") {
                            Icon(painter = painterResource(id = R.drawable.back), contentDescription = null, modifier = Modifier.size(24.dp).graphicsLayer(rotationZ = 180f))
                        } else {
                            Text(text = item, fontSize = 24.sp, fontFamily = SatoshiBold)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontFamily = SatoshiBold,
                color = if (isSelected) Color(0xFF00964E) else Color.Gray
            )
        }
    }
}

@Composable
fun TransferSuccessContent(
    state: TransfersContract.State,
    onEvent: (TransfersContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.transfer_sent),
            fontFamily = SatoshiBold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF00B159), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = state.amount,
                fontFamily = SatoshiBold,
                fontSize = 48.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "so'm",
                fontFamily = SatoshiBold,
                fontSize = 24.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Komissiya 0 so'm",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                fontSize = 14.sp,
                fontFamily = SatoshiMedium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0066B3), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.selectedToCard?.ownerName ?: "Soatmurod X",
                        fontFamily = SatoshiMedium,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "• ${state.selectedToCard?.cardNumber?.takeLast(4) ?: "1843"}",
                        fontFamily = SatoshiBold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.completed),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontFamily = SatoshiMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(R.string.add_to_template),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier.width(80.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    painter = painterResource(R.drawable.malumotnoma),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(R.string.transfer_details),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier.width(80.dp)
                )
            }
        }

        Button(
            onClick = { onEvent(TransfersContract.Intent.OnBackClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B159))
        ) {
            Text(
                text = stringResource(R.string.ready),
                fontFamily = SatoshiBold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
@Composable
fun TransferOptionItem(
    title: String,
    desc: String,
    icons: List<Int>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontFamily = SatoshiBold,
                fontSize = 15.sp
            )
            Text(
                text = desc,
                fontFamily = SatoshiMedium,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                icons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(24.dp, 16.dp)
                            .background(Color(0xFFF1F3F5), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
