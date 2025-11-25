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

package org.jraf.linez0rz9000.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import org.jraf.linez0rz9000.engine.Engine


private val emptyRunningPausedColor = Color.Black
private val emptyGameOverColor = Color.DarkGray

private val pieceRunningColor = Color.Red
private val piecePausedGameOverColor = Color.LightGray

private const val alphaDecrement = 0.25f
private val pieceRunningColorAlpha1 = pieceRunningColor.copy(alpha = 1F - alphaDecrement)
private val piecePausedGameOverColorAlpha1 = piecePausedGameOverColor.copy(alpha = 1F - alphaDecrement)
private val pieceRunningColorAlpha2 = pieceRunningColor.copy(alpha = 1F - 2 * alphaDecrement)
private val piecePausedGameOverColorAlpha2 = piecePausedGameOverColor.copy(alpha = 1F - 2 * alphaDecrement)
private val pieceRunningColorAlpha3 = pieceRunningColor.copy(alpha = 1F - 3 * alphaDecrement)
private val piecePausedGameOverColorAlpha3 = piecePausedGameOverColor.copy(alpha = 1F - 3 * alphaDecrement)


private val debrisRunningColor = Color.Green

private val shadowRunningColor = pieceRunningColor.copy(alpha = .25F).compositeOver(emptyRunningPausedColor)
private val shadowPausedColor = piecePausedGameOverColor.copy(alpha = .25F).compositeOver(emptyRunningPausedColor)
private val shadowGameOverColor = piecePausedGameOverColor.copy(alpha = .25F).compositeOver(emptyGameOverColor)

fun emptyColor(state: Engine.State): Color = when (state) {
  Engine.State.Running, Engine.State.Paused -> emptyRunningPausedColor
  Engine.State.GameOver -> emptyGameOverColor
}

fun pieceColor(state: Engine.State, index: Int = 0): Color = when (state) {
  Engine.State.Running -> when (index) {
    0 -> pieceRunningColor
    1 -> pieceRunningColorAlpha1
    2 -> pieceRunningColorAlpha2
    else -> pieceRunningColorAlpha3
  }

  Engine.State.Paused, Engine.State.GameOver -> when (index) {
    0 -> piecePausedGameOverColor
    1 -> piecePausedGameOverColorAlpha1
    2 -> piecePausedGameOverColorAlpha2
    else -> piecePausedGameOverColorAlpha3
  }
}

fun debrisColor(state: Engine.State): Color = when (state) {
  Engine.State.Running -> debrisRunningColor
  Engine.State.Paused, Engine.State.GameOver -> piecePausedGameOverColor
}

fun shadowColor(state: Engine.State): Color = when (state) {
  Engine.State.Running -> shadowRunningColor
  Engine.State.Paused -> shadowPausedColor
  Engine.State.GameOver -> shadowGameOverColor
}
