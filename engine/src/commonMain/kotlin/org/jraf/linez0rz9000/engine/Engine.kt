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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Engine(
  width: Int = 10,
  height: Int = 22,
  nextPiecesSize: Int = 4,
  private val delay: Duration = .2.seconds,
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

  val nextPieces = NextPieces(size = nextPiecesSize)

  private var piece = MutableStateFlow<PieceWithPosition>(nextPiece())

  val board: StateFlow<Board> = combine(
    _board,
    piece,
    _state,
  ) { board, piece, state ->
    if (state == State.GameOver) {
      // Don't show the current piece if the game is over
      board
    } else {
      board.withPiece(piece, Cell.Piece)
    }
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

  private val scheduler = SingleJobScheduler()

  val actionHandler = object : ActionHandler {
    override fun onLeftPressed() {
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x - 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece

        if (!pieceCanGo(movedPiece.copy(y = movedPiece.y + 1))) {
          scheduler.schedule(delay * 3) {
            gameLoop()
          }
        }
      }
    }

    override fun onRightPressed() {
      if (_state.value != State.Running) return
      val currentPiece = piece.value
      val movedPiece = currentPiece.copy(x = currentPiece.x + 1)
      if (pieceCanGo(movedPiece)) {
        piece.value = movedPiece

        if (!pieceCanGo(movedPiece.copy(y = movedPiece.y + 1))) {
          scheduler.schedule(delay * 3) {
            gameLoop()
          }
        }
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
      if (pieceCanGo((piece.value.copy(y = piece.value.y + 1)))) {
        movePieceDown()
      }
    }

    override fun onDropPressed() {
      if (_state.value != State.Running) return
      while (pieceCanGo((piece.value.copy(y = piece.value.y + 1)))) {
        movePieceDown()
      }
      scheduler.schedule(Duration.ZERO) {
        gameLoop()
      }
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
          this.piece.value = candidatePiece

          if (!pieceCanGo(candidatePiece.copy(y = candidatePiece.y + 1))) {
            scheduler.schedule(delay * 3) {
              gameLoop()
            }
          }
          return
        }
      }
    }
  }

  private fun nextPiece(): PieceWithPosition {
    val piece = nextPieces.getNextPiece()
    return PieceWithPosition(
      piece = piece,
      x = _board.value.width / 2 - 2,
      y = 2 - piece.shape(0).bottomMost(),
      rotation = 0,
    )
  }

  fun start() {
    if (_state.value != State.Running) error("Wrong state: ${_state.value}")
    scheduler.schedule(delay) {
      gameLoop()
    }
  }

  private fun gameLoop() {
    if (_state.value == State.GameOver) return

    if (!pieceCanGo((piece.value.copy(y = piece.value.y + 1)))) {
      handlePieceLanded()
      scheduler.schedule(delay) {
        gameLoop()
      }
    } else {
      movePieceDown()
      val nextDelay = if (!pieceCanGo((piece.value.copy(y = piece.value.y + 1)))) {
        delay * 3
      } else {
        delay
      }
      scheduler.schedule(nextDelay) {
        gameLoop()
      }
    }
  }

  fun pause() {
    if (_state.value != State.Running) error("Wrong state: ${_state.value}")
    _state.value = State.Paused
    scheduler.cancel()
  }

  fun unpause() {
    if (_state.value != State.Paused) error("Wrong state: ${_state.value}")
    _state.value = State.Running
    scheduler.schedule(delay) {
      gameLoop()
    }
  }

  fun stop() {
    if (_state.value == State.GameOver) error("Wrong state: ${_state.value}")
    _state.value = State.GameOver
    scheduler.cancel()
  }

  fun restart() {
    if (_state.value != State.GameOver) error("Wrong state: ${_state.value}")
    _board.value = MutableBoard(_board.value.width, _board.value.height)
    piece.value = nextPiece()
    _state.value = State.Running
    start()
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

  private fun movePieceDown() {
    val currentPiece = piece.value
    piece.value = currentPiece.copy(y = currentPiece.y + 1)
  }

  private fun handlePieceLanded() {
    // The piece is now debris
    _board.value = _board.value.withPiece(piece.value, Cell.Debris)

    removeLines()

    val nextPiece = nextPiece()
    if (!pieceCanGo(nextPiece)) {
      // Game over!
      stop()
    } else {
      piece.value = nextPiece
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
