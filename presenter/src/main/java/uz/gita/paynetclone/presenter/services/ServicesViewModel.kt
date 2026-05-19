package uz.gita.paynetclone.presenter.services

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor() : ViewModel(), ServicesContract.ViewModel {
    private val _uiState = MutableStateFlow(ServicesContract.UiState())
    override val uiState: StateFlow<ServicesContract.UiState> = _uiState.asStateFlow()

    override fun onEventDispatcher(intent: ServicesContract.Intent) {
        when (intent) {
            is ServicesContract.Intent.OpenService -> {
                // handle navigation to specific service
            }
        }
    }
}
