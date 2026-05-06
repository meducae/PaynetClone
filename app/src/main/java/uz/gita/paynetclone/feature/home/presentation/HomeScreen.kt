package uz.gita.paynetclone.feature.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.R
import uz.gita.paynetclone.ui.theme.PaynetCloneTheme

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = getViewModel()
        val state by viewModel.state.collectAsState()
        
        val navigator = LocalNavigator.currentOrThrow

        androidx.compose.runtime.LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { sideEffect ->
                when (sideEffect) {
                    HomeContract.SideEffect.NavigateToSettings -> {
                        // navigator.push(SettingsScreen())
                    }
                    HomeContract.SideEffect.NavigateToChat -> {
                        // navigator.push(ChatScreen())
                    }
                }
            }
        }

        HomeScreenContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun HomeScreenContent(
    state: HomeContract.State,
    onEvent: (HomeContract.Intent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PaynetBottomNavigation() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            PaynetTopHeader(
                onSettingsClicked = { onEvent(HomeContract.Intent.OnSettingsClicked) },
                onChatClicked = { onEvent(HomeContract.Intent.OnChatClicked) }
            )
            BalanceSection(
                isVisible = state.isBalanceVisible,
                onToggleVisibility = { onEvent(HomeContract.Intent.ToggleBalanceVisibility) }
            )
            PromotionCarousel()
            StatsGridSection()
            CardsListSection()
            HomeServicesSection()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PaynetTopHeader(onSettingsClicked: () -> Unit, onChatClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrowback),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "+998 91 234 19 48", 
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier
                        .background(
                            Color(0xFFFFF3CD), // Custom warning color
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.partially_protected),
                        fontSize = 10.sp,
                        color = Color(0xFF856404) // Custom warning text
                    )
                }
            }
        }
        Row {
            IconButton(onClick = onSettingsClicked) {
                Icon(painterResource(R.drawable.settings), null, tint = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(onClick = onChatClicked) {
                Icon(painterResource(R.drawable.chat), null, tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun BalanceSection(isVisible: Boolean, onToggleVisibility: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.my_money), color = MaterialTheme.colorScheme.secondary)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isVisible) stringResource(R.string.zero_som) else "••••••",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onToggleVisibility) {
                    Icon(painterResource(R.drawable.face), null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.transfer_to_card), color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.weight(1f))
                    Icon(painterResource(R.drawable.email), null, tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Composable
fun PromotionCarousel() {
    val items = listOf(
        Pair(stringResource(R.string.promo_visa_direct), R.drawable.ic_launcher_background),
        Pair(stringResource(R.string.promo_crypto), R.drawable.ic_launcher_background),
        Pair(stringResource(R.string.promo_kindergarten), R.drawable.ic_launcher_background),
        Pair(stringResource(R.string.promo_goodness), R.drawable.ic_launcher_background)
    )

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(140.dp, 100.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(item.second),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = item.first,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsGridSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = stringResource(R.string.stat_cashback),
            value = stringResource(R.string.zero_som),
            subValue = stringResource(R.string.stat_today_zero),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(R.string.stat_coins),
            value = "0",
            subValue = stringResource(R.string.stat_exchange),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(title: String, value: String, subValue: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subValue, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }
    }
}

@Composable
fun CardsListSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.my_cards), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { /* TODO */ }) {
                Text(text = stringResource(R.string.add_plus), color = MaterialTheme.colorScheme.primary)
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = stringResource(R.string.zero_som), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = stringResource(R.string.paynet_card_desc), color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HomeServicesSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.my_homes), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { /* TODO */ }) {
                Text(text = stringResource(R.string.add_plus), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun PaynetBottomNavigation() {
    // Note: To properly support Voyager bottom navigation, we would implement TabNavigation. 
    // This is a visual representation based on current code, using Voyager TabNavigator is recommended for production.
    var selected by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    val items = listOf(
        stringResource(R.string.nav_main),
        stringResource(R.string.nav_transfers),
        stringResource(R.string.nav_payment),
        stringResource(R.string.nav_history),
        stringResource(R.string.nav_services)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEachIndexed { index, title ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { selected = index },
                icon = { Icon(painterResource(R.drawable.phonecall), null) },
                label = { Text(title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
