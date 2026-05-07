package uz.gita.paynetclone.presenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.presenter.R
import uz.gita.paynetclone.presenter.ui.theme.SatoshiBold
import uz.gita.paynetclone.presenter.ui.theme.SatoshiMedium

@Composable
fun BalanceSection(
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
                    Text(
                        text = if (isVisible) stringResource(R.string.zero_som) else "••••••",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(top = 14.dp, bottom = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(3.dp).padding(horizontal = 10.dp)) {
                    Text(stringResource(R.string.transfer_to_card), fontFamily = SatoshiBold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.weight(1f))
                    Icon(painterResource(R.drawable.contact_book), null, modifier = Modifier.size(26.dp), tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}