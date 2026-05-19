package uz.gita.paynetclone.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.ui.theme.PaynetCloneTheme
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PaynetCardItem(
    card: Card,
    modifier: Modifier = Modifier,
    onCopyClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00964E),
                        Color(0xFF00C853)
                    )
                )
            )
    ) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF69F0AE))
                )
                Text(
                    text = card.type,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = SatoshiMedium,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "${formatBalance(card.balance)} ${stringResource(R.string.som)}",
                color = Color.White,
                fontFamily = SatoshiBold,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatCardNumber(card.maskedNumber),
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = SatoshiMedium,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onCopyClick(card.maskedNumber) }
                        .padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.copy_svgrepo_com),
                        contentDescription = "Copy",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatBalance(balance: Long): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
    return DecimalFormat("#,###", symbols).format(balance)
}

private fun formatCardNumber(number: String): String {
    return number.chunked(4).joinToString(" ")
}

@Preview
@Composable
fun PaynetCardItemPreview() {
    PaynetCloneTheme {
        PaynetCardItem(
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
            )
        )
    }
}
