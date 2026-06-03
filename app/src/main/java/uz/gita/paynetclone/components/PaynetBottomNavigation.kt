package uz.gita.paynetclone.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import uz.gita.paynetclone.screens.home.HomeTab
import uz.gita.paynetclone.screens.transfers.TransfersTab
import uz.gita.paynetclone.screens.payment.PaymentTab
import uz.gita.paynetclone.screens.history.HistoryTab
import uz.gita.paynetclone.screens.services.ServicesTab
import uz.gita.paynetclone.ui.theme.SatoshiMedium

@Composable
fun PaynetBottomNavigation() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        TabNavigationItem(HomeTab)
        TabNavigationItem(TransfersTab)
        TabNavigationItem(PaymentTab)
        TabNavigationItem(HistoryTab)
        TabNavigationItem(ServicesTab)
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title, modifier = Modifier.size(24.dp)) },
        label = { Text(tab.options.title, fontSize = 11.sp, fontFamily = SatoshiMedium) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.secondary,
            unselectedTextColor = MaterialTheme.colorScheme.secondary,
        )
    )
}
