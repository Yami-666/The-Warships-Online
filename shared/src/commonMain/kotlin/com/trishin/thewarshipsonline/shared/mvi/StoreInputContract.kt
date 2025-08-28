package com.trishin.thewarshipsonline.shared.mvi

interface StoreInputContract<Intent> {
    fun accept(intent: Intent)
}
