package com.trishin.thewarshipsonline.shared

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
