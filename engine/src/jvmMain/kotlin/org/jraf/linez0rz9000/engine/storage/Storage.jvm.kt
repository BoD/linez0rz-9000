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
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece

actual fun Storage(path: String?): Storage {
  return JvmStorage(path!!)
}

private class JvmStorage(private val path: String) : Storage {
  private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath { path.toPath() }

  override suspend fun getBoard(): Board? =
    dataStore.data.map { preferences ->
      preferences[KEY_BOARD]?.toBoard()
    }.first()

  override suspend fun saveBoard(board: Board) {
    dataStore.edit { it[KEY_BOARD] = board.toStorageString() }
  }

  override suspend fun getNextPieces(): List<Piece>? =
    dataStore.data.map { preferences ->
      preferences[KEY_NEXT_PIECES]?.toNextPieces()
    }.first()

  override suspend fun saveNextPieces(nextPieces: List<Piece>) {
    dataStore.edit { it[KEY_NEXT_PIECES] = nextPieces.toStorageString() }
  }

  override suspend fun getPieceWithPosition(): Engine.PieceWithPosition? =
    dataStore.data.map { preferences ->
      preferences[KEY_PIECE_WITH_POSITION]?.toPieceWithPosition()
    }.first()

  override suspend fun savePieceWithPosition(pieceWithPosition: Engine.PieceWithPosition) {
    dataStore.edit { it[KEY_PIECE_WITH_POSITION] = pieceWithPosition.toStorageString() }
  }

  override suspend fun getHeldPiece(): Engine.PieceWithPosition? =
    dataStore.data.map { preferences ->
      preferences[KEY_HELD_PIECE]?.toPieceWithPosition()
    }.first()

  override suspend fun saveHeldPiece(heldPiece: Engine.PieceWithPosition?) {
    dataStore.edit {
      if (heldPiece == null) {
        it.remove(KEY_HELD_PIECE)
      } else {
        it[KEY_HELD_PIECE] = heldPiece.toStorageString()
      }
    }
  }

  override suspend fun getGameLineCount(): Int? =
    dataStore.data.map { preferences ->
      preferences[KEY_LINES]
    }.first()

  override suspend fun saveGameLineCount(gameLineCount: Int) {
    dataStore.edit { it[KEY_LINES] = gameLineCount }
  }

  override suspend fun getGameLineCountMax(): Int? =
    dataStore.data.map { preferences ->
      preferences[KEY_MAX_LINES]
    }.first()

  override suspend fun saveGameLineCountMax(gameLineCountMax: Int) {
    dataStore.edit { it[KEY_MAX_LINES] = gameLineCountMax }
  }

  companion object {
    private val KEY_BOARD = stringPreferencesKey("board")
    private val KEY_NEXT_PIECES = stringPreferencesKey("nextPieces")
    private val KEY_PIECE_WITH_POSITION = stringPreferencesKey("pieceWithPosition")
    private val KEY_HELD_PIECE = stringPreferencesKey("heldPiece")
    private val KEY_LINES = intPreferencesKey("lines")
    private val KEY_MAX_LINES = intPreferencesKey("maxLines")
  }
}
