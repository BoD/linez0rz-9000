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

package org.jraf.linez0rz9000.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class Engine(
  val width: Int,
  val height: Int,
) {
  private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private var piece = MutableStateFlow<PieceWithPosition>(nextPiece())

  private val _board = MutableStateFlow<Board>(MutableBoard(width, height))

  val board: StateFlow<Board> = combine(_board, piece) { board, piece ->
    board.withPiece(piece, Cell.Piece)
  }
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.Lazily,
      initialValue = _board.value.withPiece(piece.value, Cell.Piece),
    )

  data class PieceWithPosition(
    val piece: Piece,
    val x: Int,
    val y: Int,
    val rotation: Int,
  ) {
    private fun shape(): Piece.Shape {
      return piece.shape(rotation)
    }

    fun isFilled(x: Int, y: Int) = shape().isFilled(x, y)

    fun bottomMost(x: Int) = shape().bottomMost(x)

    fun leftMost(y: Int): Int = shape().leftMost(y)

    fun rightMost(y: Int) = shape().rightMost(y)
  }

  val actionHandler = object : ActionHandler {
    override fun onLeftPressed() {
      if (!pieceCanGoLeft()) return
      piece.value = piece.value.copy(x = piece.value.x - 1)
    }

    override fun onRightPressed() {
      if (!pieceCanGoRight()) return
      piece.value = piece.value.copy(x = piece.value.x + 1)
    }

    override fun onRotateCounterClockwisePressed() {
      // Handle rotate right action
    }

    override fun onRotateClockwisePressed() {
      // Handle rotate left action
    }

    override fun onDownPressed() {
      // Handle down action
    }

    override fun onDropPressed() {
      // Handle drop action
    }

    override fun onHoldPressed() {
      // Handle hold action
    }
  }

  private fun nextPiece(): PieceWithPosition {
    val piece = Piece.values().random()
    return PieceWithPosition(
      piece = piece,
      x = width / 2 - 2,
      y = -piece.shape(0).bottomMost() - 1,
      rotation = 0,
    )
  }

  suspend fun start() {
    while (true) {
      delay(.2.seconds)

      if (!pieceCanGoDown()) {
        // The piece is now debris
        _board.value = _board.value.withPiece(piece.value, Cell.Debris)
        piece.value = nextPiece()
      } else {
        // Move the piece down
        piece.value = piece.value.copy(y = piece.value.y + 1)
      }
    }
  }

  private fun pieceCanGoDown(): Boolean {
    val piece = piece.value
    for (x in 0 until 4) {
      val bottomMost = piece.bottomMost(x)
      if (bottomMost == -1) continue
      val downY = piece.y + bottomMost + 1
      if (downY < 0) continue
      if (downY >= _board.value.height || _board.value[piece.x + x, downY] != Cell.Empty) {
        return false
      }
    }
    return true
  }

  private fun pieceCanGoLeft(): Boolean {
    val piece = piece.value
    for (y in 0 until 4) {
      if (piece.y + y < 0) continue
      val leftMost = piece.leftMost(y)
      if (leftMost == -1) continue
      val leftX = piece.x + leftMost - 1
      if (leftX < 0 || _board.value[leftX, piece.y + y] != Cell.Empty) {
        return false
      }
    }
    return true
  }

  private fun pieceCanGoRight(): Boolean {
    val piece = piece.value
    for (y in 0 until 4) {
      if (piece.y + y < 0) continue
      val rightMost = piece.rightMost(y)
      if (rightMost == -1) continue
      val rightX = piece.x + rightMost + 1
      if (rightX >= _board.value.width || _board.value[rightX, piece.y + y] != Cell.Empty) {
        return false
      }
    }
    return true
  }
}

private fun Board.withPiece(piece: Engine.PieceWithPosition, cell: Cell): Board {
  return toMutableBoard().apply {
    for (x in 0 until 4) {
      for (y in 0 until 4) {
        if (piece.isFilled(x, y)) {
          if (piece.y + y >= 0) {
            this[piece.x + x, piece.y + y] = cell
          }
        }
      }
    }
  }
}
