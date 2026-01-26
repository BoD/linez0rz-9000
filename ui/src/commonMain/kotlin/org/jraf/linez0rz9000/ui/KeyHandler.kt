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
import org.jraf.linez0rz9000.ui.KeyHandlerConfiguration.Action

class KeyHandlerConfiguration(
  val left: Set<Key>,
  val right: Set<Key>,
  val down: Set<Key>,
  val drop: Set<Key>,
  val rotateClockwise: Set<Key>,
  val rotateCounterClockwise: Set<Key>,
  val hold: Set<Key>,
  val pause: Set<Key>,
) {
  internal val mapping: Map<Key, Action> = buildMap {
    for (key in left) {
      put(key, Action.Left)
    }
    for (key in right) {
      put(key, Action.Right)
    }
    for (key in down) {
      put(key, Action.Down)
    }
    for (key in drop) {
      put(key, Action.Drop)
    }
    for (key in rotateClockwise) {
      put(key, Action.RotateClockwise)
    }
    for (key in rotateCounterClockwise) {
      put(key, Action.RotateCounterClockwise)
    }
    for (key in hold) {
      put(key, Action.Hold)
    }
    for (key in pause) {
      put(key, Action.Pause)
    }
  }

  internal enum class Action {
    Left,
    Right,
    Down,
    Drop,
    RotateClockwise,
    RotateCounterClockwise,
    Hold,
    Pause,
  }
}

val KeyboardKeyHandlerConfiguration = KeyHandlerConfiguration(
  left = setOf(Key.DirectionLeft, Key.E),
  right = setOf(Key.DirectionRight, Key.F),
  down = setOf(Key.DirectionDown, Key.D),
  drop = setOf(Key.Spacebar, Key.C),
  rotateClockwise = setOf(Key.DirectionUp, Key.X, Key.G, Key.H),
  rotateCounterClockwise = setOf(Key.ShiftRight, Key.Z, Key.J, Key.I),
  hold = setOf(Key.ShiftLeft, Key.M, Key.K),
  pause = setOf(Key.P, Key.O, Key.N),
)


@Composable
fun KeyHandler(
  engine: Engine,
  configuration: KeyHandlerConfiguration,
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
          val action = configuration.mapping[keyEvent.key]
          when (action) {
            Action.Left -> {
              actionHandler.onLeftPressed()
              true
            }

            Action.Right -> {
              actionHandler.onRightPressed()
              true
            }

            Action.Down -> {
              actionHandler.onDownPressed()
              true
            }

            Action.Drop -> {
              actionHandler.onDropPressed()
              true
            }

            Action.RotateClockwise -> {
              actionHandler.onRotateClockwisePressed()
              true
            }

            Action.RotateCounterClockwise -> {
              actionHandler.onRotateCounterClockwisePressed()
              true
            }

            Action.Hold -> {
              actionHandler.onHoldPressed()
              true
            }

            Action.Pause -> {
              actionHandler.onPausePressed()
              true
            }

            null -> false
          }
        }
      },
  )

  val state: Engine.State by engine.state.collectAsState()
  LaunchedEffect(state) {
    focusRequester.requestFocus()
  }
}
