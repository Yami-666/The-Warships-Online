 package com.trishin.thewarshipsonline

import com.trishin.thewarshipsonline.shared.UserData
import com.trishin.thewarshipsonline.shared.UserService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

 class UserServiceImpl : UserService {
  override suspend fun hello(user: String, userData: UserData): String {
    return "Nice to meet you $user, how is it in ${userData.address}?"
  }

  override fun subscribeToNews(): Flow<String> {
    return flow {
      repeat(10) {
        delay(300)
        emit("Article number $it")
      }
    }
  }
}
