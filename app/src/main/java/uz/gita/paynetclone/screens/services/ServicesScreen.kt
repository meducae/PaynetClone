package uz.gita.paynetclone.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.paynetclone.R
import uz.gita.paynetclone.components.PaynetBottomNavigation
import uz.gita.paynetclone.presenter.services.ServicesContract
import uz.gita.paynetclone.presenter.services.ServicesViewModel

class ServicesScreen : Screen {
    override val key: ScreenKey = "services.main"

    @Composable
    override fun Content() {
        val viewModel: ServicesViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()

        ServicesScreenContent(
            uiState = uiState,
            onEvent = viewModel::onEventDispatcher
        )
    }
}

@Composable
fun ServicesScreenContent(
    uiState: ServicesContract.UiState,
    onEvent: (ServicesContract.Intent) -> Unit
) {
    val services = listOf(
        ServiceItemData(
            id = "paynet_xolis",
            titleRes = R.string.services_paynet_xolis,
            subtitleRes = R.string.services_paynet_xolis_desc,
            icon = Icons.Default.Refresh,
            iconTint = Color(0xFF10B981) // Green
        ),
        ServiceItemData(
            id = "gold",
            titleRes = R.string.services_gold_investment,
            subtitleRes = R.string.services_gold_investment_desc,
            icon = Icons.Default.Star,
            iconTint = Color(0xFFD4AF37) // Gold
        ),
        ServiceItemData(
            id = "crypto",
            titleRes = R.string.services_crypto_wallet,
            subtitleRes = R.string.services_crypto_wallet_desc,
            icon = Icons.Default.ShoppingCart, // MonetizationOn could also work
            iconTint = Color(0xFFF59E0B) // Orange
        ),
        ServiceItemData(
            id = "market",
            titleRes = R.string.services_sahiy_market,
            subtitleRes = R.string.services_sahiy_market_desc,
            icon = Icons.Default.ShoppingCart,
            iconTint = Color(0xFF4B5563) // Dark Gray
        ),
        ServiceItemData(
            id = "contracts",
            titleRes = R.string.services_labor_contracts,
            subtitleRes = R.string.services_labor_contracts_desc,
            icon = Icons.Default.Email, // Representing document
            iconTint = Color(0xFF4B5563) // Dark Gray
        ),
        ServiceItemData(
            id = "nasiya",
            titleRes = R.string.services_paynet_nasiya,
            subtitleRes = R.string.services_paynet_nasiya_desc,
            icon = Icons.Default.DateRange,
            iconTint = Color(0xFF4B5563) // Dark Gray
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PaynetBottomNavigation(selectedIndex = 4) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Text(
                text = stringResource(R.string.services_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            )

            // Services List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                items(services) { service ->
                    ServiceItem(
                        data = service,
                        onClick = { onEvent(ServicesContract.Intent.OpenService(service.id)) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    data: ServiceItemData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(data.titleRes),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(data.subtitleRes),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class ServiceItemData(
    val id: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val icon: ImageVector,
    val iconTint: Color
)

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    MaterialTheme {
        ServicesScreenContent(
            uiState = ServicesContract.UiState(),
            onEvent = {}
        )
    }
}
