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

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jraf.linez0rz9000.browser

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.loadEngine
import org.jraf.linez0rz9000.engine.saveEngineState
import org.jraf.linez0rz9000.engine.storage.Storage
import org.jraf.linez0rz9000.ui.App
import org.jraf.linez0rz9000.ui.KeyHandler
import org.jraf.linez0rz9000.ui.KeyboardKeyHandlerConfiguration
import org.w3c.dom.gamepad.Gamepad
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

fun main() {
  ComposeViewport {
    val storage = Storage(null)
    val maybeEngine by produceState<Engine?>(initialValue = null) {
      value = storage.loadEngine().also { engine ->
        engine.start()
        engine.pause()
      }
    }
    val engine = maybeEngine ?: return@ComposeViewport
    val gameLineCountTo9000: Int by engine.gameLineCountTo9000.collectAsState()

    App(engine)

    KeyHandler(
      engine = engine,
      configuration = KeyboardKeyHandlerConfiguration,
    )

    LaunchedEffect(Unit) {
      // Save the storage at regular intervals
      while (true) {
        delay(5.seconds)
        storage.saveEngineState(engine = engine)
      }
    }

    LaunchedEffect(gameLineCountTo9000) {
      document.title = "Linez0rz $gameLineCountTo9000"
    }

    LaunchedEffect(Unit) {
      val pressedButtons = mutableSetOf<Int>()
      while (true) {
        awaitAnimationFrame()
        val gamepad = getGamepads()[0] ?: continue
        val newPressedButtons = mutableSetOf<Int>()
        gamepad.buttons.toArray().forEachIndexed { index, button ->
          if (button.pressed) {
            if (index !in pressedButtons) {
              pressedButtons.add(index)
              newPressedButtons.add(index)
            }
          } else {
            pressedButtons.remove(index)
          }
        }

        // Treat axes as buttons:
        // - 1000: left
        // - 1001: right
        // - 1002: up
        // - 1003: down
        gamepad.axes.toArray().take(2).forEachIndexed { axis, value ->
          val axisId1 = 1000 + axis * 2
          val axisId2 = 1000 + axis * 2 + 1
          if (value.toDouble() < -.5) {
            if (axisId1 !in pressedButtons) {
              pressedButtons.add(axisId1)
              newPressedButtons.add(axisId1)
            }
          } else if (value.toDouble() > .5) {
            if (axisId2 !in pressedButtons) {
              pressedButtons.add(axisId2)
              newPressedButtons.add(axisId2)
            }
          } else {
            pressedButtons.remove(axisId1)
            pressedButtons.remove(axisId2)
          }
        }

        // I wish there was a better way to do this. Alas, it's either a hardcoded list of known devices, or a setting...
        val invertAB = gamepad.id.contains("8BitDo", ignoreCase = true)

        for (newPressedButtonIndexes in newPressedButtons) {
          // See https://w3c.github.io/gamepad/#remapping
          when (newPressedButtonIndexes) {
            // A
            0 -> if (invertAB) {
              engine.actionHandler.onRotateClockwisePressed()
            } else {
              engine.actionHandler.onRotateCounterClockwisePressed()
            }

            // B
            1 -> if (invertAB) {
              engine.actionHandler.onRotateCounterClockwisePressed()
            } else {
              engine.actionHandler.onRotateClockwisePressed()
            }

            // RB, RT
            5, 7 -> engine.actionHandler.onHoldPressed()

            // Select, Start, Right stick
            8, 9, 11 -> engine.actionHandler.onPausePressed()

            // Up
            12, 1002 -> engine.actionHandler.onDropPressed()

            // Down
            13, 1003 -> engine.actionHandler.onDownPressed()

            // Left
            14, 1000 -> engine.actionHandler.onLeftPressed()

            // Right
            15, 1001 -> engine.actionHandler.onRightPressed()
          }
        }
      }
    }
  }
}

private suspend fun awaitAnimationFrame() = suspendCancellableCoroutine { continuation ->
  val frameId = window.requestAnimationFrame { continuation.resume(Unit) }
  continuation.invokeOnCancellation { window.cancelAnimationFrame(frameId) }
}

private fun getGamepads(): JsArray<Gamepad> = js("navigator.getGamepads()")
