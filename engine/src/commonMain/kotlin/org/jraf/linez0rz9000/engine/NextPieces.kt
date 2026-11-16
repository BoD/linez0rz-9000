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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class NextPieces {
  private val size: Int

  private val piecesBuffer: MutableList<Piece>

  private var random = Random(9000)

  internal var count: Int = 0
    private set

  val nextPieces: MutableStateFlow<List<Piece>>

  constructor(size: Int) {
    this.size = size
    this.piecesBuffer = mutableListOf<Piece>().ensureCapacity(size)
    this.nextPieces = MutableStateFlow(piecesBuffer.take(size))
  }

  constructor(pieces: List<Piece>, count: Int) {
    this.size = pieces.size
    this.piecesBuffer = mutableListOf<Piece>().ensureCapacity(size)
    this.nextPieces = MutableStateFlow(piecesBuffer.take(size))
    repeat(count) {
      getNextPiece()
    }
    this.count = count
  }

  internal fun getNextPiece(): Piece {
    count++
    return piecesBuffer.removeAt(0).also {
      piecesBuffer.ensureCapacity(size)
      nextPieces.value = piecesBuffer.take(size)
    }
  }

  internal fun reset() {
    count = 0
    random = Random(9000)
    piecesBuffer.clear()
    piecesBuffer.ensureCapacity(size)
    nextPieces.value = piecesBuffer.take(size)
  }

  private fun MutableList<Piece>.ensureCapacity(nextPiecesSize: Int): MutableList<Piece> = apply {
    if (this.size < nextPiecesSize) {
      addAll(Piece.values().shuffled(random))
    }
  }
}

