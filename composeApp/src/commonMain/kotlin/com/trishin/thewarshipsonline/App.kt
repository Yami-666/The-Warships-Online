package com.trishin.thewarshipsonline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trishin.thewarshipsonline.shared.mvi.Middleware
import com.trishin.thewarshipsonline.shared.mvi.SimpleReducer
import com.trishin.thewarshipsonline.shared.mvi.simpleStore
import com.trishin.thewarshipsonline.shared.mvi.viewmodel.SimpleStoreViewModel
import com.trishin.thewarshipsonline.shared.mvi.viewmodel.SimpleStoreViewModelFactory
import com.trishin.thewarshipsonline.shared.mvi.viewmodel.StoreViewModel
import com.trishin.thewarshipsonline.shared.services.UserData
import com.trishin.thewarshipsonline.shared.services.UserService
import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.jetbrains.compose.resources.painterResource
import thewarshipsonline.composeapp.generated.resources.Res
import thewarshipsonline.composeapp.generated.resources.compose_multiplatform

expect val DEV_SERVER_HOST: String

val client by lazy {
  HttpClient {
    installKrpc()
  }
}

@Composable
fun NetworkSampleApp() {
  var serviceOrNull: UserService? by remember { mutableStateOf(null) }

  LaunchedEffect(Unit) {
    serviceOrNull = client.rpc {
      url {
        host = DEV_SERVER_HOST
        port = 8080
        encodedPath = "/api"
      }

      rpcConfig {
        serialization {
          json()
        }
      }
    }.withService()
  }

  val service = serviceOrNull // for smart casting

  if (service != null) {
    var greeting by remember { mutableStateOf<String?>(null) }
    val news = remember { mutableStateListOf<String>() }

    LaunchedEffect(service) {
      greeting = service.hello(
        "User",
        UserData("Berlin", "Smith")
      )
    }

    LaunchedEffect(service) {
      service.subscribeToNews()
        .catch { }
        .collect { article ->
          news.add(article)
        }
    }

    MaterialTheme {
      var showIcon by remember { mutableStateOf(false) }

      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        greeting?.let {
          Text(it)
        } ?: run {
          Text("Establishing server connection...")
        }

        news.forEach {
          Text("Article: $it")
        }

        Button(onClick = { showIcon = !showIcon }) {
          Text("Click me!")
        }

        AnimatedVisibility(showIcon) {
          Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Image(painterResource(Res.drawable.compose_multiplatform), null)
          }
        }
      }
    }
  }
}

@Composable
fun MviSampleApp() {
  val store = simpleStore<HelloIntent, HelloMessage, HelloState>(
    prefix = "HelloStore",
    initialState = HelloState(message = ""),
    scope = rememberCoroutineScope(),
  ) {
    middlewares = listOf(
      Middleware.IntentHandler<HelloIntent, HelloMessage> { intent ->
        flowOf(HelloMessage)
      }
    )
    reducer = object : SimpleReducer<HelloMessage, HelloState> {
      override fun reduce(
        message: HelloMessage,
        previousState: HelloState
      ): HelloState = previousState.copy(message = "Hello world!")
    }
  }
  val storeViewModelFactory = SimpleStoreViewModelFactory { SimpleStoreViewModel(store = store) }

  val viewModel = viewModel<StoreViewModel<HelloIntent, HelloMessage, Unit, HelloState>> {
    storeViewModelFactory.create()
  }

  val state = viewModel.state.collectAsState()

  MaterialTheme {
    SomeSimpleScreen(
      state = state,
      onButtonClick = { viewModel.accept(HelloIntent) }
    )
  }
}

@Composable
private fun SomeSimpleScreen(
  state: State<HelloState>,
  onButtonClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = state.value.message)
    Button(onClick = onButtonClick) {
      Text("Click me!")
    }
  }
}

object HelloIntent
data class HelloState(val message: String)
object HelloMessage
