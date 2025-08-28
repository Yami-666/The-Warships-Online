package com.trishin.thewarshipsonline.shared.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlin.coroutines.ContinuationInterceptor

typealias SimpleStore<Intent, Message, State> = Store<Intent, Message, Unit, State>

fun <Intent, Message, Effect : Any, State> store(
  prefix: String,
  initialState: State,
  scope: CoroutineScope,
  buildBlock: StoreDsl<Intent, Message, Effect, State>.() -> Unit,
): Store<Intent, Message, Effect, State> =
  StoreDsl<Intent, Message, Effect, State>(prefix, initialState).apply(buildBlock).buildStore(scope)

fun <Intent, Message, State> simpleStore(
  prefix: String,
  initialState: State,
  scope: CoroutineScope,
  buildBlock: StoreDsl<Intent, Message, Unit, State>.() -> Unit,
): SimpleStore<Intent, Message, State> = store(prefix, initialState, scope, buildBlock)

class StoreDsl<Intent, Message, Effect : Any, State>(
  private val prefix: String,
  private val initialState: State,
) {
  var middlewares: List<Middleware<in Intent, Message, in State>> = emptyList()
  var reducer: Reducer<Message, State, Effect> = Reducer { _, state -> state }

  fun middleware(middleware: Middleware<in Intent, Message, in State>) {
    middlewares = middlewares + middleware
  }

  fun middlewares(vararg middlewares: Middleware<in Intent, Message, in State>) {
    this.middlewares += middlewares.toList()
  }

  fun buildStore(
    scope: CoroutineScope,
  ): Store<Intent, Message, Effect, State> {
    warnAboutNonMainDispatcherIfRequired(scope)
    return Store(
      initialState = initialState,
      middlewares = middlewares.toList(),
      reducer = reducer,
      prefix = prefix,
      defaultSharingScope = scope,
      sharingStrategy = SharingStarted.WhileSubscribed(0L)
    )
  }

  private fun warnAboutNonMainDispatcherIfRequired(scope: CoroutineScope) {
    val mainDispatcher = getSafely { Dispatchers.Main }
    val mainImmediateDispatcher = getSafely { mainDispatcher?.immediate }
    if (mainDispatcher == null || mainImmediateDispatcher == null) {
      return
    }

    val currentDispatcher = scope.coroutineContext[ContinuationInterceptor]
    if (currentDispatcher != mainDispatcher && currentDispatcher != mainImmediateDispatcher) {
//      Timber.w { "Обнаружен не-Main диспетчер ($currentDispatcher) для $prefix; поведение в юнит/интеграционных тестах может быть сломано" } fixme
    }
  }

  private inline fun <T> getSafely(getter: () -> T): T? = try {
    getter()
  } catch (_: IllegalStateException) {
    null
  }
}
