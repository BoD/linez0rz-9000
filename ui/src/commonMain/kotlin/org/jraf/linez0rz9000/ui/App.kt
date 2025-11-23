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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import linez0rz_9000.ui.generated.resources.Res
import linez0rz_9000.ui.generated.resources.Workbench
import org.jetbrains.compose.resources.Font
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece
import kotlin.math.min

@Composable
fun App(engine: Engine) {
  val workbenchFontFamily = FontFamily(Font(Res.font.Workbench, FontWeight.Normal))

  val board: Board by engine.board.collectAsState()
  val state: Engine.State by engine.state.collectAsState()
  val nextPieces: List<Piece> by engine.nextPieces.collectAsState()
  val heldPiece: Engine.PieceWithPosition? by engine.heldPiece.collectAsState()
  val sessionLineCount: Int by engine.sessionLineCount.collectAsState()
  val gameLineCount: Int by engine.gameLineCount.collectAsState()
  val gameLineCountMax: Int by engine.gameLineCountMax.collectAsState()
  Box(
    Modifier
      .fillMaxSize()
      .background(Color.DarkGray),
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize(),
      horizontalArrangement = Arrangement.Center,
    ) {
      Box(
        modifier = Modifier.fillMaxHeight().weight(1f, fill = false),
        contentAlignment = Alignment.Center,
      ) {
        Board(board = board, state = state)
      }

      Column(
        modifier = Modifier.width(64.dp).padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.size(8.dp))

        // Game
        Text(
          color = pieceColor(state),
          text = "Game",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$gameLineCount",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(2.dp))
        val linesTo9000 = 9000 - gameLineCount
        Text(
          color = debrisColor(state),
          text = "${if (linesTo9000 > 0) linesTo9000 else "0!!!"}",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Session
        Text(
          color = pieceColor(state),
          text = "Sess",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$sessionLineCount",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Best
        Text(
          color = pieceColor(state),
          text = "Best",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$gameLineCountMax",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )

        Spacer(modifier = Modifier.weight(1F))

        // Held piece
        if (heldPiece != null) {
          Piece(
            piece = heldPiece!!.piece,
            color = debrisColor(state),
          )

          Spacer(modifier = Modifier.weight(1F))
        }

        // Next pieces
        NextPieces(
          nextPieces = nextPieces,
          state = state,
        )

        Spacer(modifier = Modifier.size(8.dp))
      }
    }

    FourRoundButtons(
      modifier = Modifier
        .padding(start = 16.dp, bottom = 40.dp)
        .align(Alignment.BottomStart),
      buttonSize = 36.dp,
      onLeftPressed = { engine.actionHandler.onLeftPressed() },
      onRightPressed = { engine.actionHandler.onRightPressed() },
      onUpPressed = { engine.actionHandler.onDropPressed() },
      onDownPressed = { engine.actionHandler.onDownPressed() },
    )

    FourRoundButtons(
      modifier = Modifier
        .padding(end = 16.dp, bottom = 40.dp)
        .align(Alignment.BottomEnd),
      buttonSize = 36.dp,
      onLeftPressed = { engine.actionHandler.onHoldPressed() },
      onRightPressed = { engine.actionHandler.onRotateClockwisePressed() },
      onUpPressed = { engine.actionHandler.onPausePressed() },
      onDownPressed = { engine.actionHandler.onRotateCounterClockwisePressed() },
    )


    GameControlsPanel(engine = engine, state = state)
  }
}

@Composable
private fun NextPieces(
  nextPieces: List<Piece>,
  state: Engine.State,
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    for (piece in nextPieces.reversed()) {
      Piece(
        piece = piece,
        color = pieceColor(state),
      )
    }
  }
}

@Composable
private fun Piece(
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
              is Engine.State.Paused -> engine.resume()
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

