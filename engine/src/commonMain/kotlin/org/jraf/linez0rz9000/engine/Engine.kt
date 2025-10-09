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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class Engine(
  width: Int = 10,
  height: Int = 22,
) {
  sealed interface State {
    data object Running : State
    data object GameOver : State
    data object Paused : State
  }

  private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val _state = MutableStateFlow<State>(State.Running)
  val state: StateFlow<State> = _state

  private val _board = MutableStateFlow<Board>(MutableBoard(width, height))

  private var piece = MutableStateFlow<PieceWithPosition>(nextPiece())

  val board: StateFlow<Board> = combine(_board, piece) { board, piece ->
    board.withPiece(piece, Cell.Piece).hideTopLines(2)
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
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x - 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece
      }
    }

    override fun onRightPressed() {
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x + 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece
      }
    }

    override fun onRotateClockwisePressed() {
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      var rotatedPiece = currentPiece.copy(rotation = currentPiece.rotation + 1)
      applyPieceIfPossible(rotatedPiece)
    }

    override fun onRotateCounterClockwisePressed() {
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      var rotatedPiece = currentPiece.copy(rotation = currentPiece.rotation - 1)
      applyPieceIfPossible(rotatedPiece)
    }

    override fun onDownPressed() {
      if (_state.value != State.Running) return
      movePieceDown()
    }

    override fun onDropPressed() {
      if (_state.value != State.Running) return
      while (true) {
        val hasDropped = movePieceDown()
        if (hasDropped) {
          break
        }
      }
      removeLines()
      ticker.restart()
    }

    override fun onHoldPressed() {
      if (_state.value != State.Running) return
      // TODO
    }

    override fun onPausePressed() {
      when (_state.value) {
        State.Running -> {
          pause()
        }

        State.Paused -> {
          unpause()
        }

        else -> {}
      }
    }
  }

  private fun applyPieceIfPossible(piece: PieceWithPosition) {
    val yOffsets = listOf(0, -1, -2)
    val xOffsets = listOf(0, 1, 2, -1, -2)
    for (yOffset in yOffsets) {
      for (xOffset in xOffsets) {
        val candidatePiece = piece.copy(x = piece.x + xOffset, y = piece.y + yOffset)
        if (pieceCanGo(candidatePiece)) {
          this@Engine.piece.value = candidatePiece

          if (!pieceCanGo(candidatePiece.copy(y = candidatePiece.y + 1))) {
            ticker.restart()
          }
          return
        }
      }
    }
  }

  private fun nextPiece(): PieceWithPosition {
    val piece = Piece.values().random()
    return PieceWithPosition(
      piece = piece,
      x = _board.value.width / 2 - 2,
      y = 1 - piece.shape(0).bottomMost(),
      rotation = 0,
    )
  }

  private val ticker = Ticker(.5.seconds)

  fun start() {
    if (_state.value != State.Running) error("Wrong state: ${_state.value}")
    ticker.start()
    coroutineScope.launch {
      while (_state.value != State.GameOver) {
        ticker.waitTick()
        movePieceDown()
        removeLines()
      }
    }
  }

  private fun pause() {
    if (_state.value != State.Running) error("Wrong state: ${_state.value}")
    _state.value = State.Paused
    ticker.stop()
  }

  private fun unpause() {
    if (_state.value != State.Paused) error("Wrong state: ${_state.value}")
    _state.value = State.Running
    ticker.start()
  }

  fun stop() {
    if (_state.value == State.GameOver) error("Wrong state: ${_state.value}")
    _state.value = State.GameOver
    ticker.stop()
  }

  private fun removeLines() {
    var board = _board.value
    var hasChanges = false
    for (y in 0..<board.height) {
      var isFullLine = true
      for (x in 0..<board.width) {
        if (board[x, y] == Cell.Empty) {
          isFullLine = false
          break
        }
      }
      if (isFullLine) {
        hasChanges = true
        board = board.withLineRemoved(y)
      }
    }
    if (hasChanges) {
      _board.value = board
    }
  }

  private fun Board.withLineRemoved(line: Int): Board {
    return toMutableBoard().apply {
      for (y in line downTo 1) {
        for (x in 0 until width) {
          this[x, y] = this[x, y - 1]
        }
      }
      for (x in 0 until width) {
        this[x, 0] = Cell.Empty
      }
    }
  }

  private fun movePieceDown(): Boolean {
    val currentPiece = piece.value
    val movedPiece = currentPiece.copy(y = currentPiece.y + 1)
    return if (!pieceCanGo(movedPiece)) {
      // The piece is now debris
      _board.value = _board.value.withPiece(piece.value, Cell.Debris)
      val nextPiece = nextPiece()
      if (!pieceCanGo(nextPiece)) {
        stop()
      } else {
        piece.value = nextPiece
      }
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

private fun Board.hideTopLines(linesToHide: Int): Board {
  return object : Board {
    override val width: Int = this@hideTopLines.width
    override val height: Int = this@hideTopLines.height - linesToHide

    override fun get(x: Int, y: Int): Cell {
      if (y !in 0..<height) error("y out of bounds: $y")
      return this@hideTopLines[x, y + linesToHide]
    }
  }
}
