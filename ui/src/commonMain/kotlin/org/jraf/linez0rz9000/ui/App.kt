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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Cell
import org.jraf.linez0rz9000.engine.Engine

@Composable
@Preview
fun App(engine: Engine) {
  MaterialTheme {
    Surface(
      modifier = Modifier
        .safeContentPadding()
        .fillMaxSize(),
    ) {
      val board: Board by engine.board.collectAsState()
      Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width.toInt() / board.width
        val cellHeight = size.height.toInt() / board.height
        val cellSize = cellWidth.coerceAtMost(cellHeight).toFloat()
        for (y in 0..<board.height) {
          for (x in 0..<board.width) {
            val cell = board[x, y]
            drawRect(
              color = when (cell) {
                Cell.Empty -> Color.Black
                Cell.Piece -> Color.Red
                Cell.Debris -> Color.Green
              },
              topLeft = Offset(x = cellSize * x, y = cellSize * y),
              size = Size(width = cellSize - 1, height = cellSize - 1),
            )
          }
        }
      }
    }
  }
}
