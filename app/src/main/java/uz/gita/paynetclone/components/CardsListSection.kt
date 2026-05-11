package uz.gita.paynetclone.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.ui.theme.SatoshiBold
import uz.gita.paynetclone.ui.theme.SatoshiMedium
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun CardsListSection(
    cards: List<Card> = emptyList(),
    isLoading: Boolean = false,
    onClickedAdd: () -> Unit,
    onClickCards : () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClickCards() }
                ) {
                    Text(
                        text = stringResource(R.string.my_cards),
                        fontFamily = SatoshiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.down),
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                            .graphicsLayer(rotationZ = 270f),
                        tint = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClickedAdd() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_circle_fill),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF00964E)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.add_plus).replace("+ ", ""),
                        fontFamily = SatoshiMedium,
                        fontSize = 15.sp,
                        color = Color(0xFF00964E)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (isLoading && cards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                } else {
                    if (cards.isEmpty()) {
                        CardItem(
                            balance = 0,
                            desc = stringResource(R.string.paynet_card_desc),
                            onClicked = onClickCards
                        )
                    } else {
                        cards.forEachIndexed { index, card ->
                            CardItem(
                                balance = card.balance,
                                desc = "• ${card.maskedNumber.takeLast(4)} ${card.type}",
                                onClicked = onClickCards
                            )
                            if (index < cards.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardItem(
    balance: Long,
    desc: String,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF00964E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "paynet",
                color = Color.White,
                fontSize = 10.sp,
                fontFamily = SatoshiBold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${formatBalance(balance)} ${stringResource(R.string.som)}",
                fontFamily = SatoshiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontFamily = SatoshiMedium,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.down),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(rotationZ = 270f), // O'ngga ko'rsatkich
            tint = Color.LightGray
        )
    }
}

private fun formatBalance(balance: Long): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
    return DecimalFormat("#,###", symbols).format(balance).let {
        if (it.isEmpty()) "0" else it
    }
}

@Composable
@Preview(showBackground = true)
fun CardsListSectionPreview() {
    CardsListSection(
        cards = emptyList(),
        isLoading = false,
        onClickedAdd = {},
        onClickCards = {}
    )
}
