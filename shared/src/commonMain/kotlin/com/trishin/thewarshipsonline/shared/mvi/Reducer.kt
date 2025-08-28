package com.trishin.thewarshipsonline.shared.mvi

fun interface Reducer<Message, State, Effect> {

    fun reduce(message: Message, previousState: State): State
    fun reduceEffect(message: Message, previousState: State): Effect? = null

    companion object {
        val nothing = null
    }
}

fun interface SimpleReducer<Message, State> : Reducer<Message, State, Unit>
