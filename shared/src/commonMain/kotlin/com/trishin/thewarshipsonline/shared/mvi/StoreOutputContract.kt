package com.trishin.thewarshipsonline.shared.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface StoreOutputContract<State, Effect> {
    val state: StateFlow<State>
    val effects: Flow<Effect>
}
