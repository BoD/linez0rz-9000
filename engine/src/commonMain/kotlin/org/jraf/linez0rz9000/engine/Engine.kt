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
import org.jraf.linez0rz9000.engine.storage.Storage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Engine {
  private constructor(
    board: Board,
    nextPieces: NextPieces,
    pieceWithPosition: PieceWithPosition?,
    lines: Int,
    maxLines: Int,
    delay: Duration,
  ) {
    this.delay = delay
    _board = MutableStateFlow<Board>(board.toMutableBoard())
    _nextPieces = nextPieces
    this.nextPieces = _nextPieces.nextPieces
    _piece = MutableStateFlow<PieceWithPosition>(pieceWithPosition ?: nextPiece())
    this.piece = _piece
    this.board = getBoardStateFlow()

    _sessionLines = MutableStateFlow(0)
    this.sessionLines = _sessionLines

    _gameLines = MutableStateFlow(lines)
    this.gameLines = _gameLines

    _maxLines = MutableStateFlow(maxLines)
    this.maxLines = _maxLines
  }

  constructor(
    board: Board,
    nextPieces: List<Piece>,
    pieceWithPosition: PieceWithPosition?,
    lines: Int,
    maxLines: Int,
    delay: Duration = .2.seconds,
  ) : this(
    board = board,
    nextPieces = NextPieces(nextPieces),
    pieceWithPosition = pieceWithPosition,
    lines = lines,
    maxLines = maxLines,
    delay = delay,
  )

  constructor(
    width: Int = 10,
    height: Int = 22,
    nextPiecesSize: Int = 4,
    delay: Duration = .2.seconds,
  ) : this(
    board = MutableBoard(width, height),
    nextPieces = NextPieces(size = nextPiecesSize),
    pieceWithPosition = null,
    lines = 0,
    maxLines = 0,
    delay = delay,
  )

  private val delay: Duration

  sealed interface State {
    data object Running : State
    data object GameOver : State
    data object Paused : State
  }

  private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val _state = MutableStateFlow<State>(State.Paused)
  val state: StateFlow<State> = _state

  private val _board: MutableStateFlow<Board>

  private val _nextPieces: NextPieces
  val nextPieces: StateFlow<List<Piece>>

  private val _piece: MutableStateFlow<PieceWithPosition>
  val piece: StateFlow<PieceWithPosition>

  val board: StateFlow<Board>

  private val _sessionLines: MutableStateFlow<Int>
  val sessionLines: StateFlow<Int>

  private val _gameLines: MutableStateFlow<Int>
  val gameLines: StateFlow<Int>

  private val _maxLines: MutableStateFlow<Int>
  val maxLines: StateFlow<Int>

  private fun getBoardStateFlow(): StateFlow<Board> = combine(
    _board,
    _piece,
    _state,
  ) { board, piece, state ->
    if (state == State.GameOver) {
      // Don't show the current piece if the game is over
      board
    } else {
      val shadowPiece = shadowPiece(piece)
      board
        .withPiece(shadowPiece, Cell.ShadowPiece)
        .withPiece(piece, Cell.Piece)
    }
  }
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.Lazily,
      initialValue = _board.value.withPiece(_piece.value, Cell.Piece),
    )

  class PieceWithPosition(
    val piece: Piece,
    val x: Int,
    val y: Int,
    val rotation: Int,
  ) {
    private fun shape(): Piece.Shape {
      return piece.shape(rotation)
    }

    fun isFilled(x: Int, y: Int) = shape().isFilled(x, y)

    fun shifted(x: Int, y: Int): PieceWithPosition {
      return PieceWithPosition(
        piece = piece,
        x = this.x + x,
        y = this.y + y,
        rotation = rotation,
      )
    }

    fun shiftedRight(): PieceWithPosition = shifted(x = 1, y = 0)

    fun shiftedLeft(): PieceWithPosition = shifted(x = -1, y = 0)

    fun shiftedDown(): PieceWithPosition = shifted(x = 0, y = 1)

    fun rotatedClockwise(): PieceWithPosition = PieceWithPosition(
      piece = piece,
      x = x,
      y = y,
      rotation = (rotation + 1).mod(4),
    )

    fun rotatedCounterClockwise(): PieceWithPosition = PieceWithPosition(
      piece = piece,
      x = x,
      y = y,
      rotation = (rotation - 1).mod(4),
    )

    fun getRotationTests(rotationDirection: Int): List<Pair<Int, Int>> {
      return piece.getRotationTests(currentRotation = rotation, rotationDirection = rotationDirection)
    }
  }

  private val scheduler = SingleJobScheduler()

  val actionHandler = object : ActionHandler {
    override fun onLeftPressed() {
      if (_state.value != State.Running) return
      val currentPiece = _piece.value
      val movedPiece = currentPiece.shiftedLeft()
      if (pieceCanGo(movedPiece)) {
        _piece.value = movedPiece

        if (!pieceCanGo(movedPiece.shiftedDown())) {
          scheduler.schedule(delay * 3) {
            gameLoop()
          }
        }
      }
    }

    override fun onRightPressed() {
      if (_state.value != State.Running) return
      val currentPiece = _piece.value
      val movedPiece = currentPiece.shiftedRight()
      if (pieceCanGo(movedPiece)) {
        _piece.value = movedPiece

        if (!pieceCanGo(movedPiece.shiftedDown())) {
          scheduler.schedule(delay * 3) {
            gameLoop()
          }
        }
      }
    }

    override fun onRotateClockwisePressed() {
      if (_state.value != State.Running) return
      val currentPiece = _piece.value
      val rotationTests = currentPiece.getRotationTests(1)
      val rotatedPiece = currentPiece.rotatedClockwise()
      applyPieceIfPossible(rotatedPiece, rotationTests)
    }

    override fun onRotateCounterClockwisePressed() {
      if (_state.value != State.Running) return
      val currentPiece = _piece.value
      val rotationTests = currentPiece.getRotationTests(-1)
      val rotatedPiece = currentPiece.rotatedCounterClockwise()
      applyPieceIfPossible(rotatedPiece, rotationTests)
    }

    override fun onDownPressed() {
      if (_state.value != State.Running) return
      if (pieceCanGo((_piece.value.shiftedDown()))) {
        movePieceDown()
      }
    }

    override fun onDropPressed() {
      if (_state.value != State.Running) return
      while (pieceCanGo((_piece.value.shiftedDown()))) {
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
          resume()
        }

        else -> {}
      }
    }
  }

  private fun applyPieceIfPossible(piece: PieceWithPosition, rotationTests: List<Pair<Int, Int>>) {
    for ((testOffsetX, testOffsetY) in rotationTests) {
      val candidatePiece = piece.shifted(x = testOffsetX, y = -testOffsetY) // Invert Y offset because it is upwards in rotationTests
      if (pieceCanGo(candidatePiece)) {
        this._piece.value = candidatePiece
        if (!pieceCanGo(candidatePiece.shiftedDown())) {
          scheduler.schedule(delay * 3) {
            gameLoop()
          }
        }
        return
      }
    }
  }

  private fun nextPiece(): PieceWithPosition {
    val piece = _nextPieces.getNextPiece()
    return PieceWithPosition(
      piece = piece,
      x = _board.value.width / 2 - 2,
      y = 2 - piece.shape(0).bottomMost,
      rotation = 0,
    )
  }

  fun start() {
    if (_state.value == State.Running) error("Wrong state: ${_state.value}")
    _state.value = State.Running
    scheduler.schedule(delay) {
      gameLoop()
    }
  }

  private fun gameLoop() {
    if (_state.value == State.GameOver) return

    if (!pieceCanGo((_piece.value.shiftedDown()))) {
      handlePieceLanded()
      scheduler.schedule(delay) {
        gameLoop()
      }
    } else {
      movePieceDown()
      val nextDelay = if (!pieceCanGo((_piece.value.shiftedDown()))) {
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
    if (_state.value != State.Running) return
    _state.value = State.Paused
    scheduler.cancel()
  }

  fun resume() {
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
    _piece.value = nextPiece()
    _sessionLines.value = 0
    _gameLines.value = 0
    start()
  }

  private fun removeLines() {
    var board = _board.value
    var hasChanges = false
    var linesRemoved = 0
    for (y in 0..<board.height) {
      var isFullLine = true
      for (x in 0..<board.width) {
        if (board[x, y] == Cell.Empty) {
          isFullLine = false
          break
        }
      }
      if (isFullLine) {
        linesRemoved++
        hasChanges = true
        board = board.withLineRemoved(y)
      }
    }
    if (hasChanges) {
      _sessionLines.value += linesRemoved
      _gameLines.value += linesRemoved
      if (_gameLines.value > _maxLines.value) {
        _maxLines.value = _gameLines.value
      }
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
    val currentPiece = _piece.value
    _piece.value = currentPiece.shiftedDown()
  }

  private fun handlePieceLanded() {
    // The piece is now debris
    _board.value = _board.value.withPiece(_piece.value, Cell.Debris)

    removeLines()

    val nextPiece = nextPiece()
    if (!pieceCanGo(nextPiece)) {
      // Game over!
      // TODO I think this is incorrect, see https://harddrop.com/wiki/Top_out
      // See also https://harddrop.com/wiki/Spawn_Location
      stop()
    } else {
      _piece.value = nextPiece
    }
  }

  private fun pieceCanGo(piece: PieceWithPosition): Boolean {
    for (y in 0..<4) {
      for (x in 0..<4) {
        if (piece.isFilled(x, y)) {
          val boardX = piece.x + x
          val boardY = piece.y + y
          val board = _board.value
          if (boardX < 0 || boardX >= board.width || boardY >= board.height || board[boardX, boardY] != Cell.Empty) {
            return false
          }
        }
      }
    }
    return true
  }

  private fun shadowPiece(piece: PieceWithPosition): PieceWithPosition {
    var p = piece
    while (true) {
      val candidatePiece = p.shiftedDown()
      if (!pieceCanGo(candidatePiece)) {
        return p
      }
      p = candidatePiece
    }
  }

  private fun Board.withPiece(piece: PieceWithPosition, cell: Cell): Board {
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
}


suspend fun Storage.saveEngineState(engine: Engine) {
  saveBoard(engine.board.value)
  saveNextPieces(engine.nextPieces.value)
  savePieceWithPosition(engine.piece.value)
  saveLines(engine.gameLines.value)
  saveMaxLines(engine.maxLines.value)
}

suspend fun Storage.loadEngine(): Engine {
  val savedBoard = getBoard()
  val savedNextPieces = getNextPieces()
  val savedPieceWithPosition = getPieceWithPosition()
  val savedLines = getLines()
  val savedMaxLines = getMaxLines()
  return if (savedBoard != null && savedNextPieces != null && savedPieceWithPosition != null && savedLines != null && savedMaxLines != null) {
    Engine(
      board = savedBoard,
      nextPieces = savedNextPieces,
      pieceWithPosition = savedPieceWithPosition,
      lines = savedLines,
      maxLines = savedMaxLines,
    )
  } else {
    Engine()
  }
}
