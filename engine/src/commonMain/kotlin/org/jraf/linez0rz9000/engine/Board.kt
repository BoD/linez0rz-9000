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

interface Board {
  val width: Int
  val height: Int
  operator fun get(x: Int, y: Int): Cell
}

internal fun Board.toMutableBoard(): MutableBoard {
  return MutableBoard(this)
}

internal class MutableBoard(
  override val width: Int,
  override val height: Int,
) : Board {
  private val board: Array<Cell> = Array(width * height) { Cell.Empty }

  constructor(board: Board) : this(board.width, board.height) {
    for (x in 0 until width) {
      for (y in 0 until height) {
        this[x, y] = board[x, y]
      }
    }
  }

  override operator fun get(x: Int, y: Int) = board[y * width + x]
  operator fun set(x: Int, y: Int, cell: Cell) {
    board[y * width + x] = cell
  }
}
