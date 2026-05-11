package uz.gita.paynetclone.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.ui.theme.SatoshiMedium

@Composable
fun PaynetTopHeader(
    phoneNumber: String?,
    onProfileClicked: () -> Unit,
    onQrClicked: () -> Unit,
    onChatClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onProfileClicked
            )
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.user_3_line),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.background
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                if (phoneNumber == null) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                } else {
                    Text(
                        text = phoneNumber,
                        fontFamily = SatoshiMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.inversePrimary,
                            RoundedCornerShape(50)
                        )
                        .height(20.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.shield),
                        modifier = Modifier.size(10.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(R.string.partially_protected),
                        fontSize = 10.sp,
                        fontFamily = SatoshiMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
        Row {
            IconButton(onClick = onQrClicked) {
                Icon(
                    painterResource(R.drawable.qr_code),
                    null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
            IconButton(onClick = onChatClicked) {
                Icon(
                    painterResource(R.drawable.chat_3_line),
                    null,
                    modifier = Modifier.size(24.dp).scale(1.25f),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
    }
}
