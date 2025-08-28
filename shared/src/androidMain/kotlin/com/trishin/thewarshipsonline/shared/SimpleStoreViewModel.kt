package com.trishin.thewarshipsonline.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trishin.thewarshipsonline.shared.mvi.Store
import com.trishin.thewarshipsonline.shared.mvi.StoreViewModelContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

typealias SimpleStoreViewModel<Intent, Message, State> = StoreViewModel<Intent, Message, Unit, State>

class StoreViewModel<Intent, Message, Effect, State>(
    private val store: Store<Intent, Message, Effect, State>,
    sharingStrategy: SharingStarted = SharingStarted.Eagerly,
) : ViewModel(), StoreViewModelContract<Intent, Effect, State> {

    override val state: StateFlow<State> by lazy(LazyThreadSafetyMode.NONE) {
        store.stateIn(viewModelScope, sharingStrategy, store.state)
    }

    override val effects: Flow<Effect> by lazy(LazyThreadSafetyMode.NONE) {
        store.effects.receiveAsFlow()
    }

    override fun accept(intent: Intent) {
        viewModelScope.launch {
            store.emit(intent)
        }
    }
}
