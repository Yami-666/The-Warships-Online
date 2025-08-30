package com.trishin.thewarshipsonline.shared.services

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
  val address: String,
  val lastName: String,
)
