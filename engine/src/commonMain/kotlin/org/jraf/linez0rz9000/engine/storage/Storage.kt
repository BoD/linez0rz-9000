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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Cell
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.MutableBoard
import org.jraf.linez0rz9000.engine.Piece

class Storage(private val path: String) {
  private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath { path.toPath() }

  suspend fun getBoard(): Board? =
    dataStore.data.map { preferences ->
      preferences[KEY_BOARD]?.toBoard()
    }.first()

  suspend fun saveBoard(board: Board) {
    dataStore.edit { it[KEY_BOARD] = board.toStorageString() }
  }

  suspend fun getNextPieces(): List<Piece>? =
    dataStore.data.map { preferences ->
      preferences[KEY_NEXT_PIECES]?.toNextPieces()
    }.first()

  suspend fun saveNextPieces(nextPieces: List<Piece>) {
    dataStore.edit { it[KEY_NEXT_PIECES] = nextPieces.toStorageString() }
  }

  suspend fun getPieceWithPosition(): Engine.PieceWithPosition? =
    dataStore.data.map { preferences ->
      preferences[KEY_PIECE_WITH_POSITION]?.toPieceWithPosition()
    }.first()

  suspend fun savePieceWithPosition(pieceWithPosition: Engine.PieceWithPosition) {
    dataStore.edit { it[KEY_PIECE_WITH_POSITION] = pieceWithPosition.toStorageString() }
  }

  private fun Board.toStorageString(): String {
    return buildString {
      for (y in 0..<height) {
        for (x in 0..<width) {
          append(if (this@toStorageString[x, y] == Cell.Debris) '#' else ' ')
        }
        append('\n')
      }
    }
  }

  private fun String.toBoard(): Board {
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

  private fun List<Piece>.toStorageString(): String {
    return this.joinToString(separator = "") { it.name.toString() }
  }

  private fun String.toNextPieces(): List<Piece> {
    return this.map { char ->
      Piece.fromName(char)
    }
  }

  private fun Engine.PieceWithPosition.toStorageString(): String {
    return "${piece.name}:${x}:${y}:${rotation}"
  }

  private fun String.toPieceWithPosition(): Engine.PieceWithPosition {
    val parts = this.split(":")
    return Engine.PieceWithPosition(
      piece = Piece.fromName(parts[0][0]),
      x = parts[1].toInt(),
      y = parts[2].toInt(),
      rotation = parts[3].toInt(),
    )
  }

  companion object {
    private val KEY_BOARD = stringPreferencesKey("board")
    private val KEY_NEXT_PIECES = stringPreferencesKey("nextPieces")
    private val KEY_PIECE_WITH_POSITION = stringPreferencesKey("pieceWithPosition")
  }
}

