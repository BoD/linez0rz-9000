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

@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class NextPieces private constructor(
  private val nextPiecesSize: Int,
  private val piecesBuffer: MutableList<Piece>,
) : List<Piece> by piecesBuffer.subList(0, nextPiecesSize) {

  constructor(size: Int) : this(size, mutableListOf<Piece>().ensureCapacity(size))

  internal fun getNextPiece(): Piece {
    return piecesBuffer.removeFirst().also {
      piecesBuffer.ensureCapacity(nextPiecesSize)
    }
  }
}

private fun MutableList<Piece>.ensureCapacity(nextPiecesSize: Int): MutableList<Piece> = apply {
  if (this.size < nextPiecesSize) {
    addAll(Piece.values().shuffled())
  }
}
