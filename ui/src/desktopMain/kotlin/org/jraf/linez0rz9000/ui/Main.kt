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

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jraf.linez0rz9000.engine.Engine

fun main() = application {
  val engine = Engine()
  engine.start()
  GlobalScope.launch {
    engine.state.collect {
      println("State change $it")
    }
  }

  Window(
    onCloseRequest = ::exitApplication,
    title = "linez0rz 9000",
    onKeyEvent = { keyEvent ->
      if (keyEvent.type != KeyEventType.KeyDown) return@Window false
      when (keyEvent.key) {
        Key.DirectionLeft -> {
          engine.actionHandler.onLeftPressed()
          true
        }

        Key.DirectionRight -> {
          engine.actionHandler.onRightPressed()
          true
        }

        Key.DirectionDown -> {
          engine.actionHandler.onDownPressed()
          true
        }

        Key.Spacebar -> {
          engine.actionHandler.onDropPressed()
          true
        }

        Key.DirectionUp -> {
          engine.actionHandler.onRotateClockwisePressed()
          true
        }

        Key.Z -> {
          engine.actionHandler.onRotateCounterClockwisePressed()
          true
        }

        Key.X -> {
          engine.actionHandler.onRotateClockwisePressed()
          true
        }

        Key.P -> {
          engine.actionHandler.onPausePressed()
          true
        }

        else -> false
      }
    },
  ) {
    App(engine)
  }
}
