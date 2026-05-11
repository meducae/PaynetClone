package uz.gita.paynetclone.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.CardEvent
import uz.gita.paynetclone.core.common.CardEventBus
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.usecase.auth.LogoutUseCase
import uz.gita.paynetclone.usecase.card.GetCardsUseCase
import uz.gita.paynetclone.usecase.user.GetProfileUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager,
    private val authEventBus: AuthEventBus,
    private val cardEventBus: CardEventBus
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeContract.SideEffect>()
    val sideEffect: SharedFlow<HomeContract.SideEffect> = _sideEffect.asSharedFlow()

    init {
        onEvent(HomeContract.Intent.LoadProfile)
        loadCards()
        listenCardEvents()
    }

    private fun listenCardEvents() {
        viewModelScope.launch {
            cardEventBus.events.collectLatest { event ->
                when (event) {
                    CardEvent.CardAdded -> {
                        loadCards()
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeContract.Intent) {
        when(event) {
            HomeContract.Intent.ToggleBalanceVisibility -> {
                _state.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
            }
            HomeContract.Intent.OnSettingsClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToSettings)
                }
            }
            HomeContract.Intent.OnAddedClicked ->{
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToAddCard)
                }
            }
            HomeContract.Intent.OnMyCardsClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToMyCards)
                }
            }
            HomeContract.Intent.OnChatClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToChat)
                }
            }
            HomeContract.Intent.LoadProfile -> {
                loadProfile()
                loadCards()
            }
            HomeContract.Intent.Refresh -> {
                loadProfile()
                loadCards()
            }
            HomeContract.Intent.Logout -> {
                logout()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            tokenManager.clearTokens()
            authEventBus.logout()
        }
    }

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getProfileUseCase()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            getCardsUseCase()
                .collect { result ->
                    result.onSuccess { cards ->
                        _state.update { it.copy(cards = cards) }
                    }.onFailure { throwable ->
                        _state.update { it.copy(error = throwable.message) }
                    }
                }
        }
    }
}
