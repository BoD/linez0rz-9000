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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import org.jraf.linez0rz9000.engine.Engine

@Composable
fun KeyHandler(
  engine: Engine,
  gamepadMode: Boolean,
  gamepadInvertAB: Boolean,
) {
  val actionHandler = engine.actionHandler
  val focusRequester = remember { FocusRequester() }
  Box(
    modifier = Modifier
      .fillMaxSize()
      .focusRequester(focusRequester)
      .focusTarget()
      .onKeyEvent { keyEvent ->
        if (keyEvent.type != KeyEventType.KeyDown) {
          false
        } else {
          when (keyEvent.key) {
            Key.DirectionLeft,
            Key.E,
              -> {
              actionHandler.onLeftPressed()
              true
            }

            Key.DirectionRight,
            Key.F,
              -> {
              actionHandler.onRightPressed()
              true
            }

            Key.DirectionUp -> {
              if (gamepadMode) {
                actionHandler.onDropPressed()
              } else {
                actionHandler.onRotateClockwisePressed()
              }
              true
            }

            Key.Spacebar,
            Key.C,
              -> {
              actionHandler.onDropPressed()
              true
            }

            Key.DirectionDown,
            Key.D,
              -> {
              actionHandler.onDownPressed()
              true
            }

            Key.ButtonB,
              -> {
              if (gamepadInvertAB) {
                actionHandler.onRotateCounterClockwisePressed()
              } else {
                actionHandler.onRotateClockwisePressed()
              }
              true
            }

            Key.ButtonA,
              -> {
              if (gamepadInvertAB) {
                actionHandler.onRotateClockwisePressed()
              } else {
                actionHandler.onRotateCounterClockwisePressed()
              }
              true
            }

            Key.X,
            Key.G,
            Key.H,
              -> {
              actionHandler.onRotateClockwisePressed()
              true
            }

            Key.Z,
            Key.J,
            Key.I,
            Key.ShiftRight,
              -> {
              actionHandler.onRotateCounterClockwisePressed()
              true
            }

            Key.ButtonSelect,
            Key.ButtonStart,
            Key.P,
            Key.O,
              -> {
              actionHandler.onPausePressed()
              true
            }

            Key.ButtonR1,
            Key.ButtonR2,
            Key.ShiftLeft,
              -> {
              actionHandler.onHoldPressed()
              true
            }

            else -> {
              false
            }
          }
        }
      },
  )

  val state: Engine.State by engine.state.collectAsState()
  LaunchedEffect(state) {
    focusRequester.requestFocus()
  }
}
