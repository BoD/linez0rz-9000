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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece
import kotlin.math.min

@Composable
fun App(engine: Engine) {
  val board: Board by engine.board.collectAsState()
  val state: Engine.State by engine.state.collectAsState()
  val nextPieces: List<Piece> by engine.nextPieces.collectAsState()
  Row {
    Box(
      modifier = Modifier
        .weight(1F),
    ) {
      Board(board = board, state = state)
    }

    NextPieces(
      nextPieces = nextPieces,
      state = state,
    )
  }
  GameControlsPanel(engine = engine, state = state)
}

@Composable
private fun NextPieces(
  nextPieces: List<Piece>,
  state: Engine.State,
) {
  Column(
    modifier = Modifier
      .padding(top = 24.dp, bottom = 24.dp)
      .width(64.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    for (piece in nextPieces) {
      NextPiece(
        piece = piece,
        state = state,
      )
    }
  }
}

@Composable
private fun NextPiece(
  piece: Piece,
  state: Engine.State,
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
                color = pieceColor(state),
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

@Composable
private fun GameControlsPanel(
  engine: Engine,
  state: Engine.State,
) {
  when (state) {
    is Engine.State.Running -> {
      // Nothing
    }

    is Engine.State.Paused,
    is Engine.State.GameOver,
      -> {
      Box(Modifier.fillMaxSize()) {
        Button(
          modifier = Modifier.align(Alignment.Center),
          onClick = {
            when (state) {
              is Engine.State.Paused -> engine.unpause()
              is Engine.State.GameOver -> engine.restart()
              else -> throw IllegalStateException()
            }
          },
        ) {
          Text(
            when (state) {
              is Engine.State.Paused -> "Resume"
              is Engine.State.GameOver -> "Play again"
              else -> throw IllegalStateException()
            },
          )
        }
      }
    }
  }
}

