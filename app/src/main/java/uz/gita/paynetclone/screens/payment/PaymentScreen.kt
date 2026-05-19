package uz.gita.paynetclone.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.paynetclone.R
import uz.gita.paynetclone.components.PaynetBottomNavigation
import uz.gita.paynetclone.presenter.payment.PaymentContract
import uz.gita.paynetclone.presenter.payment.PaymentViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import uz.gita.paynetclone.screens.payment.flow.PaymentAccountScreen

class PaymentScreen : Screen {
    override val key: ScreenKey = "payment.main"

    @Composable
    override fun Content() {
        val viewModel: PaymentViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()

        PaymentScreenContent(
            uiState = uiState,
            onEvent = viewModel::onEventDispatcher
        )
    }
}
@Composable
fun PaymentScreenContent(
    uiState: PaymentContract.UiState,
    onEvent: (PaymentContract.Intent) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PaynetBottomNavigation(selectedIndex = 2) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.payment_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.qr_code),
                            contentDescription = "QR Scanner",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    val isPlaces = uiState.activeTab == PaymentContract.PaymentTab.PLACES
                    TabItem(
                        text = stringResource(R.string.payment_tab_places),
                        isSelected = isPlaces,
                        onClick = { onEvent(PaymentContract.Intent.SetTab(PaymentContract.PaymentTab.PLACES)) },
                        modifier = Modifier.weight(1f)
                    )
                    TabItem(
                        text = stringResource(R.string.payment_tab_templates),
                        isSelected = !isPlaces,
                        onClick = { onEvent(PaymentContract.Intent.SetTab(PaymentContract.PaymentTab.TEMPLATES)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.activeTab == PaymentContract.PaymentTab.PLACES) {
                    PlacesContent(
                        uiState = uiState,
                        onEvent = onEvent,
                        onCategoryClick = { categoryId, categoryName ->
                            navigator.push(uz.gita.paynetclone.screens.payment.flow.PaymentCategoryProvidersScreen(categoryId, categoryName))
                        },
                        onProviderClick = { provider ->
                            navigator.push(PaymentAccountScreen(provider.id, provider.name))
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No templates", color = MaterialTheme.colorScheme.secondary)
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun PlacesContent(
    uiState: PaymentContract.UiState,
    onEvent: (PaymentContract.Intent) -> Unit,
    onCategoryClick: (String, String) -> Unit,
    onProviderClick: (uz.gita.paynetclone.entity.payment.Provider) -> Unit
) {
    val allInstitutionsText = stringResource(R.string.payment_all_institutions)
    val communicationText = stringResource(R.string.payment_type_communication)
    val internetTvText = stringResource(R.string.payment_type_internet_tv)
    val communalText = stringResource(R.string.payment_type_communal)
    val educationText = stringResource(R.string.payment_type_education)
    val stateServicesText = stringResource(R.string.payment_type_state_services)
    val transportText = stringResource(R.string.payment_type_transport)
    val loanText = stringResource(R.string.payment_type_loan)
    val microcreditText = stringResource(R.string.payment_type_microcredit)
    val otherText = stringResource(R.string.payment_type_other)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { onCategoryClick("all", allInstitutionsText) }
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = allInstitutionsText,
                        color = Color(0xFF10B981),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        uiState.providers.take(10).forEach { provider ->
            PlaceCard(
                title = provider.name,
                distance = provider.category,
                logoColor = Color(0xFFE5E7EB),
                onClick = { onProviderClick(provider) }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.payment_types),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 20.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentCategoryCard(
                titleRes = R.string.payment_type_communication,
                backgroundColor = Color(0xFFFDE6B4),
                height = 140.dp,
                painter = painterResource(R.drawable.phonecall),
                iconColor = Color(0xFFE5B55C),
                iconSize = 64.dp,
                titleColor = Color.Black,
                onClick = { onCategoryClick("mobile", communicationText) }
            )
            PaymentCategoryCard(
                titleRes = R.string.payment_type_internet_tv,
                backgroundColor = Color(0xFFC2E0F9),
                height = 110.dp,
                painter = painterResource(R.drawable.widgets_outline),
                iconColor = Color(0xFF86BCEB),
                titleColor = Color.Black,
                onClick = { onCategoryClick("Internet", internetTvText) }
            )
            PaymentCategoryCard(
                titleRes = R.string.payment_type_education,
                backgroundColor = Color(0xFFFAD4D4),
                height = 110.dp,
                painter = painterResource(R.drawable.shield),
                iconColor = Color(0xFFE5A4A4),
                titleColor = Color.Black,
                onClick = { onCategoryClick("education", educationText) }
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentCategoryCard(
                titleRes = R.string.payment_type_requisite,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                height = 70.dp,
                painter = painterResource(R.drawable.shield),
                iconColor = Color.Transparent,
                titleColor = MaterialTheme.colorScheme.onBackground,
                onClick = { }
            )
            PaymentCategoryCard(
                titleRes = R.string.payment_type_communal,
                backgroundColor = Color(0xFFC1E8CE),
                height = 130.dp,
                painter = painterResource(R.drawable.shield),
                iconColor = Color(0xFF8BCCA0),
                iconSize = 56.dp,
                titleColor = Color.Black,
                onClick = { onCategoryClick("utility", communalText) }
            )
            PaymentCategoryCard(
                titleRes = R.string.payment_type_state_services,
                backgroundColor = Color(0xFFE5D9F2),
                height = 148.dp,
                painter = painterResource(R.drawable.malumotnoma),
                iconColor = Color(0xFFBCA6D9),
                iconSize = 64.dp,
                titleColor = Color.Black,
                onClick = { onCategoryClick("state_services", stateServicesText) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallCategoryCard(
                titleRes = R.string.payment_type_installment,
                iconText = "%",
                modifier = Modifier.weight(1f),
                onClick = { }
            )
            SmallCategoryCard(
                titleRes = R.string.payment_type_transport,
                painter = painterResource(R.drawable.shield),
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("transport", transportText) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallCategoryCard(
                titleRes = R.string.payment_type_loan,
                painter = painterResource(R.drawable.shield),
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("loan", loanText) }
            )
            SmallCategoryCard(
                titleRes = R.string.payment_type_medicine,
                iconText = "+",
                modifier = Modifier.weight(1f),
                onClick = { }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallCategoryCard(
                titleRes = R.string.payment_type_microcredit,
                painter = painterResource(R.drawable.shield),
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("microcredit", microcreditText) }
            )
            SmallCategoryCard(
                titleRes = R.string.payment_type_other,
                painter = painterResource(R.drawable.shield),
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("other", otherText) }
            )
        }
    }

    Spacer(modifier = Modifier.height(30.dp))
}

@Composable
fun PlaceCard(title: String, distance: String, logoColor: Color, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(logoColor)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = distance,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp
                    )
                }
            }
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun PaymentCategoryCard(
    titleRes: Int,
    backgroundColor: Color,
    height: Dp,
    painter: Painter?,
    iconColor: Color,
    titleColor: Color,
    iconSize: Dp = 48.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Text(
            text = stringResource(titleRes),
            color = titleColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp),
            lineHeight = 20.sp
        )
        
        if (painter != null) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
            )
        }
    }
}

@Composable
fun SmallCategoryCard(
    titleRes: Int,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    iconText: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(titleRes),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            } else if (iconText != null) {
                Text(
                    text = iconText,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    MaterialTheme {
        PaymentScreenContent(
            uiState = PaymentContract.UiState(),
            onEvent = {}
        )
    }
}
