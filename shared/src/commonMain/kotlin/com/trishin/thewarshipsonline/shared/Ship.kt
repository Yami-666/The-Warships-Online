package com.trishin.thewarshipsonline.shared

data class Ship(
  val x: Int,
  val y: Int,
  val length: Int,
  val isDead: Boolean = false,
  val isHorizontal: Boolean,
)
