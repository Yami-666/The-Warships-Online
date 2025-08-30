package com.trishin.thewarshipsonline.shared.services

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface UserService {
  suspend fun hello(user: String, userData: UserData): String

  fun subscribeToNews(): Flow<String>
}
