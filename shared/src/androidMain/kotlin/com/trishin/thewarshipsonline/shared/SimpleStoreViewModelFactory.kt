package com.trishin.thewarshipsonline.shared

typealias SimpleStoreViewModelFactory<Intent, Message, State> = StoreViewModelFactory<Intent, Message, Unit, State>

fun interface StoreViewModelFactory<Intent, Message, Effect, State> {
    fun create(): StoreViewModel<Intent, Message, Effect, State>
}
