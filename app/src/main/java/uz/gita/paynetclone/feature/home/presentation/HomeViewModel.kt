package uz.gita.paynetclone.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeContract.SideEffect>()
    val sideEffect: SharedFlow<HomeContract.SideEffect> = _sideEffect.asSharedFlow()

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
            HomeContract.Intent.OnChatClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToChat)
                }
            }
        }
    }
}
