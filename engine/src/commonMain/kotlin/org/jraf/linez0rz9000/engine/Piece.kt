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

sealed class Piece(
  shapes: List<List<String>>,
) {
  class Shape(private val array: BooleanArray) {
    fun isFilled(x: Int, y: Int): Boolean {
      return array[y * 4 + x]
    }

    fun bottomMost(): Int {
      for (y in 3 downTo 0) {
        for (x in 0 until 4) {
          if (array[y * 4 + x]) {
            return y
          }
        }
      }
      return -1
    }

    fun bottomMost(x: Int): Int {
      for (y in 3 downTo 0) {
        if (array[y * 4 + x]) {
          return y
        }
      }
      return -1
    }

    fun leftMost(y: Int): Int {
      for (x in 0 until 4) {
        if (array[y * 4 + x]) {
          return x
        }
      }
      return -1
    }

    fun rightMost(y: Int): Int {
      for (x in 3 downTo 0) {
        if (array[y * 4 + x]) {
          return x
        }
      }
      return -1
    }
  }

  private val shapes: List<Shape> = shapes.map { it.toShape() }

  fun shape(rotation: Int): Shape {
    return shapes[rotation % shapes.size]
  }

  private fun List<String>.toShape(): Shape {
    val array = BooleanArray(16)
    for (y in indices) {
      for (x in this[y].indices) {
        array[y * 4 + x] = this[y][x] == '#'
      }
    }
    return Shape(array)
  }

  companion object {
    fun values(): List<Piece> {
      return listOf(
        I, O, T, S, Z, J, L,
      )
    }
  }

  object I : Piece(
    listOf(
      listOf(
        "    ",
        "####",
        "    ",
        "    ",
      ),

      listOf(
        "  # ",
        "  # ",
        "  # ",
        "  # ",
      ),

      listOf(
        "    ",
        "    ",
        "####",
        "    ",
      ),

      listOf(
        " #  ",
        " #  ",
        " #  ",
        " #  ",
      ),
    ),
  )

  object O : Piece(
    listOf(
      listOf(
        "    ",
        " ## ",
        " ## ",
        "    ",
      ),
    ),
  )


  object T : Piece(
    listOf(
      listOf(
        " #  ",
        "### ",
        "    ",
        "    ",
      ),

      listOf(
        " #  ",
        " ## ",
        " #  ",
        "    ",
      ),

      listOf(
        "    ",
        "### ",
        " #  ",
        "    ",
      ),

      listOf(
        " #  ",
        "##  ",
        " #  ",
        "    ",
      ),
    ),
  )

  object S : Piece(
    listOf(
      listOf(
        " ## ",
        "##  ",
        "    ",
        "    ",
      ),

      listOf(
        " #  ",
        " ## ",
        "  # ",
        "    ",
      ),

      listOf(
        "    ",
        " ## ",
        "##  ",
        "    ",
      ),

      listOf(
        "#   ",
        "##  ",
        " #  ",
        "    ",
      ),
    ),

    )

  object Z : Piece(
    listOf(
      listOf(
        "##  ",
        " ## ",
        "    ",
        "    ",
      ),

      listOf(
        "  # ",
        " ## ",
        " #  ",
        "    ",
      ),

      listOf(
        "    ",
        "##  ",
        " ## ",
        "    ",
      ),

      listOf(
        " #  ",
        "##  ",
        "#   ",
        "    ",
      ),
    ),
  )

  object J : Piece(
    listOf(
      listOf(
        "#   ",
        "### ",
        "    ",
        "    ",
      ),
      listOf(
        " ## ",
        " #  ",
        " #  ",
        "    ",
      ),
      listOf(
        "    ",
        "### ",
        "  # ",
        "    ",
      ),
      listOf(
        " #  ",
        " #  ",
        "##  ",
        "    ",
      ),
    ),
  )

  object L : Piece(
    listOf(
      listOf(
        "  # ",
        "### ",
        "    ",
        "    ",
      ),
      listOf(
        " #  ",
        " #  ",
        " ## ",
        "    ",
      ),
      listOf(
        "    ",
        "### ",
        "#   ",
        "    ",
      ),

      listOf(
        "##  ",
        " #  ",
        " #  ",
        "    ",
      ),
    ),
  )
}

