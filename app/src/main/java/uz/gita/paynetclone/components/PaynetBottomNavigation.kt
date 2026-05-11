package uz.gita.paynetclone.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.gita.paynetclone.R
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator
import uz.gita.paynetclone.ui.theme.SatoshiMedium

@Composable
fun PaynetBottomNavigation(selectedIndex: Int = 0) {
    val navigator = LocalAppNavigator.current
    val items = listOf<Pair<String, Int>>(
        Pair(stringResource(R.string.nav_main), R.drawable.home_4_fill),
        Pair(stringResource(R.string.nav_transfers), R.drawable.repeat),
        Pair(stringResource(R.string.nav_payment), R.drawable.wallet_3_line),
        Pair(stringResource(R.string.nav_history), R.drawable.time),
        Pair(stringResource(R.string.nav_services), R.drawable.widgets_outline)
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    if (selectedIndex == index) return@NavigationBarItem
                    when (index) {
                        0 -> navigator.openHome()
                        1 -> navigator.openTransfers()
                        2 -> navigator.openPayment()
                        3 -> navigator.openHistory()
                        4 -> navigator.openServices()
                        // Qolgan ekranlar qo'shilganda bu yerga qo'shiladi
                    }
                },
                icon = { Icon(painterResource(item.second), null, modifier = Modifier.size(24.dp)) },
                label = { Text(item.first, fontSize = 11.sp, fontFamily = SatoshiMedium) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                )
            )
        }
    }
}

@Composable
@Preview
fun PaynetBottomNavigationPreview() {
    PaynetBottomNavigation()
}
