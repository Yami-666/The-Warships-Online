package com.trishin.thewarshipsonline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.trishin.thewarshipsonline.shared.Ship

@Composable
fun GameFieldScreen(
  mofidier: Modifier = Modifier,
) {
  val screenWidthPx = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp().toPx()  }
  val screenHeightPx = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp().toPx()  }

  val cellSizePx = minOf(screenWidthPx, screenHeightPx) / 14
  val strokeWidth = with(LocalDensity.current) { 0.5.dp.toPx() }
  val blueAreaStrokeWidth = with(LocalDensity.current) { 2.dp.toPx() }

  Box(
    modifier = mofidier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val startX = (14 - 10) / 2 * cellSizePx
      val startY = (14 - 10) / 2 * cellSizePx
      val blueAreaWidth = 10 * cellSizePx
      val blueAreaHeight = 10 * cellSizePx

      drawRect(
        color = Color.Blue,
        topLeft = Offset(startX, startY),
        size = Size(blueAreaWidth, blueAreaHeight),
        style = Stroke(width = blueAreaStrokeWidth)
      )

      drawRect(
        color = Color.Blue,
        topLeft = Offset(startX, startY + blueAreaHeight + cellSizePx * 2),
        size = Size(blueAreaWidth, blueAreaHeight),
        style = Stroke(width = blueAreaStrokeWidth)
      )

      listOf(
        Ship(
          x = 1,
          y = 1,
          length = 1,
          isHorizontal = true
        ),
        Ship(
          x = 4,
          y = 2,
          length = 4,
          isHorizontal = true
        ),
        Ship(
          x = 5,
          y = 7,
          length = 2,
          isHorizontal = true
        ),
        Ship(
          x = 3,
          y = 4,
          length = 3,
          isHorizontal = true
        ),
      ).forEach { (oX, oY, length, _, isHorizontal) ->
        drawRect(
          color = Color.Blue,
          topLeft = Offset(startX + oX * cellSizePx, startY + oY * cellSizePx),
          size = if (isHorizontal) {
            Size(cellSizePx * length, cellSizePx)
          } else {
            Size(cellSizePx, cellSizePx * length)
          },
          style = Stroke(width = blueAreaStrokeWidth)
        )
      }

      drawCircle(
        color = Color.Blue,
        radius = 6.dp.toPx(),
        center = Offset(startX + cellSizePx / 2, startY + cellSizePx / 2)
      )

      drawCircle(
        color = Color.Blue,
        radius = 6.dp.toPx(),
        center = Offset(
          startX + (9 * cellSizePx) + cellSizePx / 2,
          startY + (5 * cellSizePx) + cellSizePx / 2
        )
      )

      listOf(
        4 to 5,
        5 to 5,
        6 to 5,
      ).forEach { (oX, oY) ->
        val (x, y) = ((oX - 1) * cellSizePx) to ((oY - 1) * cellSizePx)
        drawLine(
          color = Color.Blue,
          start = Offset(startX + x, startY + y),
          end = Offset(startX + +x + cellSizePx, startY + y + cellSizePx),
          strokeWidth = blueAreaStrokeWidth
        )

        drawLine(
          color = Color.Blue,
          start = Offset(startX + x + cellSizePx, startY + y),
          end = Offset(startX + x, startY + y + cellSizePx),
          strokeWidth = blueAreaStrokeWidth
        )
      }

      drawBackground(strokeWidth, cellSizePx)
    }
  }
}

private fun DrawScope.drawBackground(
  strokeWidth: Float,
  cellSizePx: Float
) {
  var x = 0f
  while (x <= size.width) {
    drawLine(
      color = Color.Black,
      start = Offset(x, 0f),
      end = Offset(x, size.height),
      strokeWidth = strokeWidth
    )
    x += cellSizePx
  }

  var y = 0f
  while (y <= size.height) {
    drawLine(
      color = Color.Black,
      start = Offset(0f, y),
      end = Offset(size.width, y),
      strokeWidth = strokeWidth
    )
    y += cellSizePx
  }
}
