package com.trishin.thewarshipsonline.shared.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(
  ExperimentalCoroutinesApi::class,
  ExperimentalAtomicApi::class
)
class Store<Intent, Message, Effect, State>(
  initialState: State,
  private val middlewares: List<Middleware<in Intent, Message, in State>>,
  private val reducer: Reducer<Message, State, Effect>,
  private val prefix: String,
  defaultSharingScope: CoroutineScope,
  sharingStrategy: SharingStarted = SharingStarted.Eagerly,
) : AbstractFlow<State>(), FlowCollector<Intent> {

  private val _intentFlow = MutableSharedFlow<Intent>()
  private val _effectChannel = Channel<Effect>()

  val effects: ReceiveChannel<Effect> = _effectChannel

  private val atomicState = AtomicReference(initialState)
  var state: State
    get() = atomicState.load()
    private set(value) {
      atomicState.store(value)
    }

  private val allMessageFlow = MutableSharedFlow<Message>()

  private val context = object : MiddlewareContext<Intent, Message, State> {
    override val intents: Flow<Intent> = _intentFlow.logIntents()
    override val messages: Flow<Message> = allMessageFlow
    override val state: State
      get() = this@Store.state
  }

  private val _internalState =
    observeMiddlewares()
      .logMessages()
      .onEach { allMessageFlow.emit(it) }
      .onEach { message -> produceEffects(message, state) }
      .map { message -> reducer.reduce(message, state) }
      .logStates()
      .onEach { state = it }
      .onStart { emit(state) }
      .shareIn(defaultSharingScope, sharingStrategy)

  private suspend fun produceEffects(message: Message, state: State) {
    val effect = reducer.reduceEffect(message, state)
//      ?.also { Timber.d(TAG + prefix) { "Post effect=$it" } } fixme
    if (effect != null) {
      _effectChannel.send(effect)
    }
  }

  private fun observeMiddlewares(): Flow<Message> =
    middlewares
      .map { middleware -> middleware.runOn(context) }
      .merge()

  override suspend fun collectSafely(collector: FlowCollector<State>) {
    _internalState.collect(collector::emit)
  }

  override suspend fun emit(value: Intent) {
//    Timber.d(TAG + prefix) { "New intent emitted: $value" } fixme
    _intentFlow.emit(value)
  }

  private fun <Message> Flow<Message>.logMessages() =
    onEach {
//      Timber.d(TAG + prefix) { "Message: $it" } fixme
    }

  internal fun <Intent> Flow<Intent>.logIntents() =
    onEach {
//      Timber.d(TAG + prefix) { "Handling intent=$it" } fixme
    }

  private fun <State> Flow<State>.logStates() =
    onEach {
//      Timber.d(TAG + prefix) { "Reduce state $it" } fixme
    }
}
