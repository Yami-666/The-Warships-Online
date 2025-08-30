package com.trishin.thewarshipsonline.shared.mvi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge

fun interface Middleware<Intent, Message, State> {

    fun MiddlewareContext<Intent, Message, State>.apply(): Flow<Message>

    fun interface Bootstrap<Message> : Middleware<Any, Message, Any> {

        fun bootstrap(): Flow<Message>

        override fun MiddlewareContext<Any, Message, Any>.apply(): Flow<Message> = bootstrap()
    }

    fun interface IntentHandler<Intent, Message> : Middleware<Intent, Message, Any> {

        suspend fun process(intent: Intent): Flow<Message>

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun MiddlewareContext<Intent, Message, Any>.apply(): Flow<Message> =
            intents.flatMapMerge { intent -> process(intent) }
    }
}

interface MiddlewareContext<out Intent, Message, out State> {
    val state: State
    val intents: Flow<Intent>
    val messages: Flow<Message>
}

fun <Intent, Message, State> Middleware<Intent, Message, State>.runOn(
    context: MiddlewareContext<Intent, Message, State>
) = context.apply()
