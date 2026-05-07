package uz.gita.paynetclone.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun logout() {
        _events.emit(AuthEvent.Logout)
    }
}

sealed class AuthEvent {
    object Logout : AuthEvent()
}
