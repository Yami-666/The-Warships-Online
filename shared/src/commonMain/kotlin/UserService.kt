import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
  val address: String,
  val lastName: String,
)

@Rpc
interface UserService {
  suspend fun hello(user: String, userData: UserData): String

  fun subscribeToNews(): Flow<String>
}
