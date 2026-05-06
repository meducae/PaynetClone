package uz.gita.paynetclone.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import kotlin.collections.listOf

val SatoshiMedium = FontFamily(
    Font(R.font.satoshimedium)
)
val SatoshiBold = FontFamily(
    Font(R.font.satoshibold)
)


@Composable
fun PasswordScreen() {
    var pin by remember { mutableStateOf("") }
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
            Icon(
                painter = painterResource(R.drawable.lockicon),
                contentDescription = "Lock",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PIN-kodni kiriting",
            fontSize = 26.sp,
            fontFamily = SatoshiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Telefon raqamingiz",
            fontSize = 15.sp,
            color = Color.Gray
        )
        Text(
            text = "+998 91 ••• •• 82",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(82.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 4) {
                val dotColor = if (i < pin.length) Color(0xFF22C55E) else Color(0xFFF1F5F9)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color = dotColor, shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Numpad(
            onNumberClick = { number ->
                if (pin.length < 4) {
                    pin += number
                }
            },
            onForgotClick = {
            },
            onBiometricClick = {
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun Numpad(
    onNumberClick: (String) -> Unit,
    onForgotClick: () -> Unit,
    onBiometricClick: () -> Unit
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
                    NumberButton(number = number, onClick = { onNumberClick(number) })
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
                    text = "Kodni unut-\ndingizmi?",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                NumberButton(number = "0", onClick = { onNumberClick("0") })
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onBiometricClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.face),
                    contentDescription = "Face ID",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

    }

}

@Composable
fun NumberButton(number: String, onClick: () -> Unit) {
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

@Composable
@Preview
fun PasswordScreenPreview() {
    PasswordScreen()
}