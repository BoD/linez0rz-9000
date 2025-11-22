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

package org.jraf.linez0rz9000.engine.storage

import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Cell
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.MutableBoard
import org.jraf.linez0rz9000.engine.Piece

interface Storage {
  suspend fun getBoard(): Board?
  suspend fun saveBoard(board: Board)

  suspend fun getNextPieces(): List<Piece>?
  suspend fun saveNextPieces(nextPieces: List<Piece>)

  suspend fun getPieceWithPosition(): Engine.PieceWithPosition?
  suspend fun savePieceWithPosition(pieceWithPosition: Engine.PieceWithPosition)

  suspend fun getHeldPiece(): Engine.PieceWithPosition?
  suspend fun saveHeldPiece(heldPiece: Engine.PieceWithPosition?)

  suspend fun getGameLineCount(): Int?
  suspend fun saveGameLineCount(gameLineCount: Int)

  suspend fun getGameLineCountMax(): Int?
  suspend fun saveGameLineCountMax(gameLineCountMax: Int)
}

internal fun Board.toStorageString(): String {
  return buildString {
    for (y in 0..<height) {
      for (x in 0..<width) {
        append(if (this@toStorageString[x, y] == Cell.Debris) '#' else ' ')
      }
      append('\n')
    }
  }
}

internal fun String.toBoard(): Board {
  val lines = this.lines().filter { it.isNotEmpty() }
  val width = lines.first().length
  val height = lines.size
  return MutableBoard(width = width, height = height).also { board ->
    for (y in 0..<height) {
      for (x in 0..<width) {
        board[x, y] = if (lines[y][x] == '#') Cell.Debris else Cell.Empty
      }
    }
  }
}

internal fun List<Piece>.toStorageString(): String {
  return this.joinToString(separator = "") { it.name.toString() }
}

internal fun String.toNextPieces(): List<Piece> {
  return this.map { char ->
    Piece.fromName(char)
  }
}

internal fun Engine.PieceWithPosition.toStorageString(): String {
  return "${piece.name}:${x}:${y}:${rotation}"
}

internal fun String.toPieceWithPosition(): Engine.PieceWithPosition {
  val parts = this.split(":")
  return Engine.PieceWithPosition(
    piece = Piece.fromName(parts[0][0]),
    x = parts[1].toInt(),
    y = parts[2].toInt(),
    rotation = parts[3].toInt(),
  )
}

expect fun Storage(path: String?): Storage
