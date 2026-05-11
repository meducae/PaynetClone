package uz.gita.paynetclone.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium

import java.util.Locale

@Composable
fun BalanceSection(
    balance: Long,
    isVisible: Boolean,
    isLoading: Boolean = false,
    onToggleVisibility: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth(),
    ) {
        Column(Modifier.padding(top = 16.dp)) {
            Text(
                stringResource(R.string.my_money),
                fontFamily = SatoshiMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                } else {
                    val formattedBalance = if (isVisible) {
                        String.format(Locale.US, "%,d", balance).replace(',', ' ') + " " + stringResource(R.string.som)
                    } else {
                        "••••••"
                    }
                    Text(
                        text = formattedBalance,
                        fontSize = 30.sp,
                        fontFamily = SatoshiBold,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        painterResource(R.drawable.view),
                        modifier = Modifier.size(26.dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 1.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        stringResource(R.string.transfer_to_card),
                        fontFamily = SatoshiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        painterResource(R.drawable.contact_book),
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
