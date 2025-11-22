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

import kotlinx.browser.localStorage
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece
import org.w3c.dom.get
import org.w3c.dom.set

actual fun Storage(path: String?): Storage {
  return WebStorage()
}

private class WebStorage : Storage {
  override suspend fun getBoard(): Board? {
    return localStorage["board"]?.toBoard()
  }

  override suspend fun saveBoard(board: Board) {
    localStorage["board"] = board.toStorageString()
  }

  override suspend fun getNextPieces(): List<Piece>? {
    return localStorage["nextPieces"]?.toNextPieces()
  }

  override suspend fun saveNextPieces(nextPieces: List<Piece>) {
    localStorage["nextPieces"] = nextPieces.toStorageString()
  }

  override suspend fun getPieceWithPosition(): Engine.PieceWithPosition? {
    return localStorage["pieceWithPosition"]?.toPieceWithPosition()
  }

  override suspend fun savePieceWithPosition(pieceWithPosition: Engine.PieceWithPosition) {
    localStorage["pieceWithPosition"] = pieceWithPosition.toStorageString()
  }

  override suspend fun getHeldPiece(): Engine.PieceWithPosition? {
    return localStorage["heldPiece"]?.toPieceWithPosition()
  }

  override suspend fun saveHeldPiece(heldPiece: Engine.PieceWithPosition?) {
    if (heldPiece != null) {
      localStorage["heldPiece"] = heldPiece.toStorageString()
    } else {
      localStorage.removeItem("heldPiece")
    }
  }

  override suspend fun getGameLineCount(): Int? {
    return localStorage["gameLineCount"]?.toIntOrNull()
  }

  override suspend fun saveGameLineCount(gameLineCount: Int) {
    localStorage["gameLineCount"] = gameLineCount.toString()
  }

  override suspend fun getGameLineCountMax(): Int? {
    return localStorage["gameLineCountMax"]?.toIntOrNull()
  }

  override suspend fun saveGameLineCountMax(gameLineCountMax: Int) {
    localStorage["gameLineCountMax"] = gameLineCountMax.toString()
  }
}
