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
  }

  val actionHandler = object : ActionHandler {
    override fun onLeftPressed() {
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x - 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece
      }
    }

    override fun onRightPressed() {
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x + 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece
      }
    }

    override fun onRotateClockwisePressed() {
      val currentPiece = piece.value
      var rotatedPiece = currentPiece.copy(rotation = currentPiece.rotation + 1)
      applyPieceIfPossible(rotatedPiece)
    }

    override fun onRotateCounterClockwisePressed() {
      val currentPiece = piece.value
      var rotatedPiece = currentPiece.copy(rotation = currentPiece.rotation - 1)
      applyPieceIfPossible(rotatedPiece)
    }

    private fun applyPieceIfPossible(piece: PieceWithPosition) {
      val offsets = listOf(0, 1, 2, -1, -2)
      for (offset in offsets) {
        val candidatePiece = piece.copy(x = piece.x + offset)
        if (pieceCanGo(candidatePiece)) {
          this@Engine.piece.value = candidatePiece
          return
        }
      }
    }

    override fun onDownPressed() {
      movePieceDown()
    }

    override fun onDropPressed() {
      while (true) {
        val hasDropped = movePieceDown()
        if (hasDropped) {
          break
        }
      }
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

      movePieceDown()
    }
  }

  private fun movePieceDown(): Boolean {
    val currentPiece = piece.value
    val movedPiece = currentPiece.copy(y = currentPiece.y + 1)
    return if (!pieceCanGo(movedPiece)) {
      // The piece is now debris
      _board.value = _board.value.withPiece(piece.value, Cell.Debris)
      piece.value = nextPiece()
      true
    } else {
      // Move the piece down
      piece.value = movedPiece
      false
    }
  }

  private fun pieceCanGo(piece: PieceWithPosition): Boolean {
    for (y in 0..<4) {
      for (x in 0..<4) {
        if (piece.isFilled(x, y)) {
          val boardX = piece.x + x
          val boardY = piece.y + y
          if (boardY < 0) continue
          val board = _board.value
          if (boardX < 0 || boardX >= board.width || boardY >= board.height || board[boardX, boardY] != Cell.Empty) {
            return false
          }
        }
      }
    }
    return true
  }
}

private fun Board.withPiece(piece: Engine.PieceWithPosition, cell: Cell): Board {
  return toMutableBoard().apply {
    for (x in 0..<4) {
      for (y in 0..<4) {
        if (piece.isFilled(x, y)) {
          if (piece.y + y >= 0) {
            this[piece.x + x, piece.y + y] = cell
          }
        }
      }
    }
  }
}
