import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.catch
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
fun App() {
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
        "User from ${getPlatform().name} platform",
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
