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
    return shapes[rotation]
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
      listOf(
        "    ",
        " ## ",
        " ## ",
        "    ",
      ),
      listOf(
        "    ",
        " ## ",
        " ## ",
        "    ",
      ),
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

// See https://harddrop.com/wiki/SRS
// /!\ Positive y is upwards here, unlike the rest of the codebase /!\
private val JLSTZRotationTests = listOf(
  // 0
  listOf(
    // 0 -> 1
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(-1, 1),
      Pair(0, -2),
      Pair(-1, -2),
    ),
    // 0 -> 3
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(1, 1),
      Pair(0, -2),
      Pair(1, -2),
    ),
  ),
  // 1
  listOf(
    // 1 -> 2
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(1, -1),
      Pair(0, 2),
      Pair(1, 2),
    ),
    // 1 -> 0
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(1, -1),
      Pair(0, 2),
      Pair(1, 2),
    ),
  ),
  // 2
  listOf(
    // 2 -> 3
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(1, 1),
      Pair(0, -2),
      Pair(1, -2),
    ),
    // 2 -> 1
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(-1, 1),
      Pair(0, -2),
      Pair(-1, -2),
    ),
  ),
  // 3
  listOf(
    // 3 -> 0
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(-1, -1),
      Pair(0, 2),
      Pair(-1, 2),
    ),
    // 3 -> 2
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(-1, -1),
      Pair(0, 2),
      Pair(-1, 2),
    ),
  ),
)

private val IRotationTests = listOf(
  // 0
  listOf(
    // 0 -> 1
    listOf(
      Pair(0, 0),
      Pair(-2, 0),
      Pair(1, 0),
      Pair(-2, -1),
      Pair(1, 2),
    ),
    // 0 -> 3
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(2, 0),
      Pair(-1, 2),
      Pair(2, -1),
    ),
  ),
  // 1
  listOf(
    // 1 -> 2
    listOf(
      Pair(0, 0),
      Pair(-1, 0),
      Pair(2, 0),
      Pair(-1, 2),
      Pair(2, -1),
    ),
    // 1 -> 0
    listOf(
      Pair(0, 0),
      Pair(2, 0),
      Pair(-1, 0),
      Pair(2, 1),
      Pair(-1, -2),
    ),
  ),
  // 2
  listOf(
    // 2 -> 3
    listOf(
      Pair(0, 0),
      Pair(2, 0),
      Pair(-1, 0),
      Pair(2, 1),
      Pair(-1, -2),
    ),
    // 2 -> 1
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(-2, 0),
      Pair(1, -2),
      Pair(-2, 1),
    ),
  ),
  // 3
  listOf(
    // 3 -> 0
    listOf(
      Pair(0, 0),
      Pair(1, 0),
      Pair(-2, 0),
      Pair(1, -2),
      Pair(-2, 1),
    ),
    // 3 -> 2
    listOf(
      Pair(0, 0),
      Pair(-2, 0),
      Pair(1, 0),
      Pair(-2, -1),
      Pair(1, 2),
    ),
  ),
)

private val ORotationTests = listOf(Pair(0, 0))

fun Piece.getRotationTests(currentRotation: Int, rotationDirection: Int): List<Pair<Int, Int>> {
  return when (this) {
    Piece.J, Piece.L, Piece.S, Piece.T, Piece.Z -> JLSTZRotationTests
    Piece.I -> IRotationTests
    Piece.O -> return ORotationTests
  }[currentRotation][if (rotationDirection > 0) 0 else 1]
}
