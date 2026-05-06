package uz.gita.paynetclone.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import uz.gita.paynetclone.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ResendTimerPanel(
    timerSeconds: Int,
    isResendEnabled: Boolean,
    onResendClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.refresh),
                contentDescription = "Resend icon",
                tint = if (isResendEnabled) Color.Black else Color.Gray,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = stringResource(R.string.resend_code),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isResendEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .clickable(enabled = isResendEnabled) {
                        onResendClick()
                    }
                    .padding(horizontal = 8.dp)
            )

            Text(
                text = "$timerSeconds " + stringResource(R.string.seconds_short),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}