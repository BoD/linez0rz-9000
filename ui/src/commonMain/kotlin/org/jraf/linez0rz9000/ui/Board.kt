/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2025-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.linez0rz9000.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Cell
import org.jraf.linez0rz9000.engine.Engine
import kotlin.math.min

@Composable
internal fun BoardBackground(width: Int, height: Int, color: Color) {
  BoardCanvas(width, height) {
    drawCells(width, height) { _, _ -> color }
  }
}

@Composable
internal fun Board(width: Int, height: Int, board: State<Board>, state: Engine.State) {
  val activeColor = pieceColor(state)
  val ghostColor = shadowColor(state)
  val lockedColor = debrisColor(state)
  BoardCanvas(width, height) {
    val snapshot = board.value
    drawCells(width, height) { x, y ->
      when (snapshot[x, y]) {
        Cell.Piece -> activeColor
        Cell.ShadowPiece -> ghostColor
        Cell.Debris -> lockedColor
        Cell.Empty -> null
      }
    }
  }
}

@Composable
private fun BoardCanvas(boardWidth: Int, boardHeight: Int, onDraw: DrawScope.() -> Unit) {
  Layout(
    content = { Canvas(modifier = Modifier.fillMaxSize(), onDraw = onDraw) },
  ) { measurables, constraints ->
    // First row is hidden and row 1 is half shown
    val aspectRatio = boardWidth.toFloat() / (boardHeight.toFloat() - 1.5F)
    val constraintMaxHeight = constraints.maxHeight
    val constraintMaxWidth = constraints.maxWidth
    val maxWidthBasedOnHeight = (constraintMaxHeight * aspectRatio).toInt()
    val maxHeightBasedOnWidth = (constraintMaxWidth / aspectRatio).toInt()

    val maxWidth: Int
    val maxHeight: Int
    if (maxWidthBasedOnHeight <= constraintMaxWidth) {
      // Use height as constraint
      maxWidth = maxWidthBasedOnHeight
      maxHeight = constraintMaxHeight
    } else {
      // Use width as constraint
      maxWidth = constraintMaxWidth
      maxHeight = maxHeightBasedOnWidth
    }
    val cellMaxWidth = (maxWidth - 2) / boardWidth
    val cellMaxHeight = ((maxHeight - 2) / (boardHeight - 1.5F)).toInt()
    val cellSize = min(cellMaxWidth, cellMaxHeight)

    // We add 1 pixel for the top/left offsets
    val boardPixelWidth = boardWidth * cellSize + 1
    val boardPixelHeight = ((boardHeight - 1.5F) * cellSize).toInt() + 1

    val placeable = measurables.first().measure(Constraints.fixed(boardPixelWidth, boardPixelHeight))
    layout(boardPixelWidth, boardPixelHeight) {
      placeable.place(0, 0)
    }
  }
}

private inline fun DrawScope.drawCells(width: Int, height: Int, colorAt: (Int, Int) -> Color?) {
  // Remove 1 to account for the top/left offsets.
  val cellSize = (size.width.toInt() - 1) / width
  // First row is hidden and row 1 is half shown.
  for (y in 1..<height) {
    for (x in 0..<width) {
      val color = colorAt(x, y) ?: continue
      drawRect(
        topLeft = Offset(
          x = (1 + cellSize * x).toFloat(),
          y = (1 + if (y == 1) 0 else cellSize * (y - 2) + cellSize / 2).toFloat(),
        ),
        size = Size(
          width = (cellSize - 1).toFloat(),
          height = (if (y == 1) cellSize / 2 - 1 else cellSize - 1).toFloat(),
        ),
        color = color,
      )
    }
  }
}
