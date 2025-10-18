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

package org.jraf.linez0rz9000

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.ui.App

class MainActivity : ComponentActivity() {
  private val engine = Engine()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    engine.start()

    setContent {
      App(engine)
    }
  }

  @SuppressLint("RestrictedApi")
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.action == KeyEvent.ACTION_DOWN) {
      when (event.keyCode) {
        KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_E,
          ->
          engine.actionHandler.onLeftPressed()

        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_F,
          ->
          engine.actionHandler.onRightPressed()

        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_C,
          ->
          engine.actionHandler.onDropPressed()

        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_D,
          ->
          engine.actionHandler.onDownPressed()

        KeyEvent.KEYCODE_BUTTON_A,
        KeyEvent.KEYCODE_BUTTON_X,
        KeyEvent.KEYCODE_G,
          ->
          engine.actionHandler.onRotateClockwisePressed()

        KeyEvent.KEYCODE_BUTTON_B,
        KeyEvent.KEYCODE_BUTTON_Y,
        KeyEvent.KEYCODE_J,
          ->
          engine.actionHandler.onRotateCounterClockwisePressed()

        KeyEvent.KEYCODE_BUTTON_START,
        KeyEvent.KEYCODE_O,
          ->
          engine.actionHandler.onPausePressed()

        else ->
          return super.dispatchKeyEvent(event)
      }
      return true
    }
    return super.dispatchKeyEvent(event)
  }
}
