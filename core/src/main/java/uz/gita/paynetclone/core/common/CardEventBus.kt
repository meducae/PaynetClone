package uz.gita.paynetclone.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<CardEvent>()
    val events = _events.asSharedFlow()

    suspend fun cardAdded() {
        _events.emit(CardEvent.CardAdded)
    }
}

sealed class CardEvent {
    object CardAdded : CardEvent()
}
