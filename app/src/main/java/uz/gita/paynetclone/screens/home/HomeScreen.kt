package uz.gita.paynetclone.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import uz.gita.paynetclone.components.BalanceSection
import uz.gita.paynetclone.components.CardsListSection
import uz.gita.paynetclone.components.HomeServicesSection
import uz.gita.paynetclone.components.PaynetBottomNavigation
import uz.gita.paynetclone.components.PaynetTopHeader
import uz.gita.paynetclone.components.PromotionCarousel
import uz.gita.paynetclone.components.StatsGridSection
import uz.gita.paynetclone.presenter.home.HomeContract
import uz.gita.paynetclone.presenter.home.HomeViewModel
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator

class HomeScreen : Screen {
    override val key: ScreenKey = "home.main"

    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = getViewModel()
        val state by viewModel.state.collectAsState()

        val navigator = LocalAppNavigator.current

        LaunchedEffect(viewModel) {
            viewModel.sideEffect.collectLatest { sideEffect ->
                when (sideEffect) {
                    HomeContract.SideEffect.NavigateToSettings -> {
                        navigator.openSettings()
                    }

                    HomeContract.SideEffect.NavigateToChat -> {
                    }

                    HomeContract.SideEffect.NavigateToAddCard -> {
                        navigator.openAddCard()
                    }

                    HomeContract.SideEffect.NavigateToMyCards -> {
                        navigator.openMyCards()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeContract.State,
    onEvent: (HomeContract.Intent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PaynetBottomNavigation(selectedIndex = 0) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(HomeContract.Intent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 30.dp,
                                bottomEnd = 30.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceDim)
                        .padding(bottom = 18.dp)
                ) {
                    PaynetTopHeader(
                        phoneNumber = state.user?.phone,
                        onProfileClicked = { onEvent(HomeContract.Intent.OnSettingsClicked) },
                        onQrClicked = { },
                        onChatClicked = { onEvent(HomeContract.Intent.OnChatClicked) }
                    )
                    BalanceSection(
                        balance = state.cards.sumOf { it.balance },
                        isVisible = state.isBalanceVisible,
                        isLoading = state.isLoading,
                        onToggleVisibility = { onEvent(HomeContract.Intent.ToggleBalanceVisibility) }
                    )
                }
                PromotionCarousel()
                StatsGridSection(isLoading = state.isLoading)
                CardsListSection(
                    cards = state.cards,
                    isLoading = state.isLoading,
                    onClickedAdd = {
                        onEvent(HomeContract.Intent.OnAddedClicked)
                    },
                    onClickCards = {
                        onEvent(HomeContract.Intent.OnMyCardsClicked)
                    }
                )
                HomeServicesSection()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenContentPreview() {
    HomeScreenContent(
        state = HomeContract.State(
            isBalanceVisible = true
        ),
        onEvent = {}
    )
}
