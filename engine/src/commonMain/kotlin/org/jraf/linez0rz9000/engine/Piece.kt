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
  val name: Char,
  shapes: List<List<String>>,
) {
  class Shape(private val array: BooleanArray) {
    val topMost: Int = topMost()
    val leftMost: Int = leftMost()
    val bottomMost: Int = bottomMost()

    val width: Int
    val height: Int

    init {
      val (w, h) = dimensions()
      width = w
      height = h
    }

    fun isFilled(x: Int, y: Int): Boolean {
      return array[y * 4 + x]
    }

    private fun topMost(): Int {
      for (y in 0..<4) {
        for (x in 0..<4) {
          if (array[y * 4 + x]) {
            return y
          }
        }
      }
      error("Empty shape")
    }

    private fun bottomMost(): Int {
      for (y in 3 downTo 0) {
        for (x in 0..<4) {
          if (array[y * 4 + x]) {
            return y
          }
        }
      }
      error("Empty shape")
    }

    private fun leftMost(): Int {
      for (x in 0..<4) {
        for (y in 0..<4) {
          if (array[y * 4 + x]) {
            return x
          }
        }
      }
      error("Empty shape")
    }

    private fun dimensions(): Pair<Int, Int> {
      var maxX = -1
      var maxY = -1
      for (y in topMost..<4) {
        for (x in leftMost..<4) {
          if (array[y * 4 + x]) {
            if (x > maxX) {
              maxX = x
            }
            if (y > maxY) {
              maxY = y
            }
          }
        }
      }
      return maxX + 1 - leftMost to maxY + 1 - topMost
    }
  }

  private val shapes: List<Shape> = shapes.map { it.toShape() }

  fun shape(rotation: Int): Shape {
    return shapes[rotation.mod(shapes.size)]
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

    fun fromName(name: Char): Piece {
      return when (name) {
        'I' -> I
        'O' -> O
        'T' -> T
        'S' -> S
        'Z' -> Z
        'J' -> J
        'L' -> L
        else -> error("Unknown piece name: $name")
      }
    }
  }

  object I : Piece(
    name = 'I',
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
    name = 'O',
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
    name = 'T',
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
    name = 'S',
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
    name = 'Z',
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
    name = 'J',
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
    name = 'L',
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

