package uz.gita.paynetclone.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.gita.paynetclone.core.common.CardEventBus
import uz.gita.paynetclone.presenter.navigation.AppNavigator
import uz.gita.paynetclone.usecase.card.AttachCardUseCase
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val attachCardUseCase: AttachCardUseCase,
    private val navigator: AppNavigator,
    private val cardEventBus: CardEventBus
) : ViewModel(), AddCardContract.ViewModel {

    private val _uiState = MutableStateFlow<AddCardContract.UiState>(AddCardContract.UiState.Idle)
    override val uiState: StateFlow<AddCardContract.UiState> = _uiState.asStateFlow()

    override fun onEventDispatcher(event: AddCardContract.Intent) {
        when (event) {
            is AddCardContract.Intent.AttachCard -> {
                viewModelScope.launch {
                    _uiState.value = AddCardContract.UiState.Loading
                    attachCardUseCase(event.cardNumber).collect { result ->
                        result.onSuccess {
                            _uiState.value = AddCardContract.UiState.Success
                            cardEventBus.cardAdded()
                            navigator.back()
                        }.onFailure {
                            _uiState.value = AddCardContract.UiState.Error(it.message ?: "Unknown error")
                        }
                    }
                }
            }
            AddCardContract.Intent.Back -> {
                navigator.back()
            }
        }
    }
}
