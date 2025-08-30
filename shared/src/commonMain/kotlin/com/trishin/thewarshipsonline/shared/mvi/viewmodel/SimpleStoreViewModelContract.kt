package com.trishin.thewarshipsonline.shared.mvi.viewmodel

import com.trishin.thewarshipsonline.shared.mvi.StoreInputContract
import com.trishin.thewarshipsonline.shared.mvi.StoreOutputContract

typealias SimpleStoreViewModelContract<Intent, State> = StoreViewModelContract<Intent, Unit, State>

interface StoreViewModelContract<Intent, Effect, State> : StoreOutputContract<State, Effect>,
  StoreInputContract<Intent>
