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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece
import kotlin.math.min

@Composable
fun NextPieces(
  nextPieces: List<Piece>,
  state: Engine.State,
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    for ((index, piece) in nextPieces.reversed().withIndex()) {
      Piece(
        piece = piece,
        color = pieceColor(state, index = nextPieces.size - index - 1),
      )
    }
  }
}

@Composable
fun Piece(
  piece: Piece,
  color: Color,
) {
  Layout(
    content = {
      Canvas(
        modifier = Modifier.fillMaxSize(),
      ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cellWidth = (canvasWidth.toInt() - 2) / 4
        val cellHeight = (canvasHeight.toInt() - 2) / 2
        val cellSize = min(cellWidth, cellHeight)
        val shape = piece.shape(0)
        val leftOffset = (4 - shape.width) * cellSize / 2 - shape.leftMost * cellSize
        val topOffset = (2 - shape.height) * cellSize / 2 - shape.topMost * cellSize

        for (x in 0..<4) {
          for (y in 0..<4) {
            if (shape.isFilled(x, y)) {
              drawRect(
                topLeft = Offset(
                  x = (1 + cellSize * x + leftOffset).toFloat(),
                  y = (1 + cellSize * y + topOffset).toFloat(),
                ),
                size = Size(
                  width = (cellSize - 1).toFloat(),
                  height = (cellSize - 1).toFloat(),
                ),
                color = color,
              )
            }
          }
        }
      }
    },
  ) { measurables, constraints ->
    val width = constraints.maxWidth
    val height = width / 2
    val placeable = measurables.first().measure(Constraints.fixed(width, height))
    layout(width, height) {
      placeable.place(0, 0)
    }
  }
}
