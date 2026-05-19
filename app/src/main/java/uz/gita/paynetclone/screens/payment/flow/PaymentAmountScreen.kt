package uz.gita.paynetclone.screens.payment.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import uz.gita.paynetclone.R
import uz.gita.paynetclone.presenter.paymentflow.PaymentAmountViewModel

data class PaymentAmountScreen(
    val providerId: String,
    val providerName: String,
    val account: String
) : Screen {
    override val key: ScreenKey = "payment.amount.$providerId"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PaymentAmountViewModel = getViewModel()
        
        val cards by viewModel.cards.collectAsState()
        val selectedCard by viewModel.selectedCard.collectAsState()
        
        var amount by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                // Provider Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(providerName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(providerName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(account, fontSize = 16.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.payment_from), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                if (selectedCard != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF00796B)))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("${selectedCard?.type} • ${selectedCard?.maskedNumber?.takeLast(4)}", fontWeight = FontWeight.Medium)
                            Text("${selectedCard?.balance} so'm", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    Text("No cards available", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.payment_you_pay), fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (amount.isEmpty()) "500 - 1 500 000 so'm" else "$amount so'm",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (amount.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("3000", "5000", "15000").forEach { quickAmount ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { amount = quickAmount }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("$quickAmount so'm", fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val amountLong = amount.toLongOrNull() ?: 0L
                        if (amountLong > 0 && selectedCard != null) {
                            navigator.push(PaymentConfirmScreen(providerId, providerName, account, amountLong, selectedCard!!.id, selectedCard!!.maskedNumber))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (amount.isNotBlank() && selectedCard != null) Color(0xFF10B981) else Color(0xFFE5E7EB),
                        contentColor = if (amount.isNotBlank() && selectedCard != null) Color.White else Color.Gray
                    ),
                    enabled = amount.isNotBlank() && selectedCard != null
                ) {
                    Text(stringResource(R.string.payment_continue), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                CustomNumpad(
                    onNumberClick = { num ->
                        if (amount == "0") amount = num.toString()
                        else amount += num.toString()
                    },
                    onDeleteClick = {
                        if (amount.isNotEmpty()) amount = amount.dropLast(1)
                    }
                )
            }
        }
    }
}

@Composable
fun CustomNumpad(onNumberClick: (Int) -> Unit, onDeleteClick: () -> Unit) {
    val keys = listOf(
        "1" to "", "2" to "ABC", "3" to "DEF",
        "4" to "GHI", "5" to "JKL", "6" to "MNO",
        "7" to "PQRS", "8" to "TUV", "9" to "WXYZ",
        "" to "", "0" to "", "DEL" to ""
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(keys) { (num, letters) ->
            if (num.isEmpty()) {
                Spacer(modifier = Modifier.size(56.dp))
            } else if (num == "DEL") {
                Box(
                    modifier = Modifier.height(56.dp).clip(RoundedCornerShape(8.dp)).clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⌫", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNumberClick(num.toInt()) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(num, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                        if (letters.isNotEmpty()) {
                            Text(letters, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
