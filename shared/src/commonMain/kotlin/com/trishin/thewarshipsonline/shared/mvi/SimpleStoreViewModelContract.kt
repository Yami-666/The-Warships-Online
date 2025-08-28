package com.trishin.thewarshipsonline.shared.mvi

typealias SimpleStoreViewModelContract<Intent, State> = StoreViewModelContract<Intent, Unit, State>

interface StoreViewModelContract<Intent, Effect, State> :
    StoreOutputContract<State, Effect>, StoreInputContract<Intent>
