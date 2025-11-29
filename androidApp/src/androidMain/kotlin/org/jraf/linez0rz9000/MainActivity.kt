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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jraf.linez0rz9000.ui.App

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      val engine by viewModel.engine.collectAsState()
      if (engine != null) {
        Scaffold(containerColor = Color.Transparent) { contentPadding ->
          Box(modifier = Modifier.padding(contentPadding)) {
            App(engine!!)
          }
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    viewModel.pause()
    viewModel.saveEngineState()
  }

  @SuppressLint("RestrictedApi")
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.action != KeyEvent.ACTION_DOWN) {
      return super.dispatchKeyEvent(event)
    }

    return when (event.keyCode) {
      KeyEvent.KEYCODE_DPAD_LEFT,
      KeyEvent.KEYCODE_E,
        -> {
        viewModel.engine.value?.actionHandler?.onLeftPressed()
        true
      }

      KeyEvent.KEYCODE_DPAD_RIGHT,
      KeyEvent.KEYCODE_F,
        -> {
        viewModel.engine.value?.actionHandler?.onRightPressed()
        true
      }

      KeyEvent.KEYCODE_DPAD_UP,
      KeyEvent.KEYCODE_C,
        -> {
        viewModel.engine.value?.actionHandler?.onDropPressed()
        true
      }

      KeyEvent.KEYCODE_DPAD_DOWN,
      KeyEvent.KEYCODE_D,
        -> {
        viewModel.engine.value?.actionHandler?.onDownPressed()
        true
      }

      KeyEvent.KEYCODE_BUTTON_A,
      KeyEvent.KEYCODE_BUTTON_X,
      KeyEvent.KEYCODE_G,
      KeyEvent.KEYCODE_H,
        -> {
        viewModel.engine.value?.actionHandler?.onRotateClockwisePressed()
        true
      }

      KeyEvent.KEYCODE_BUTTON_B,
      KeyEvent.KEYCODE_BUTTON_Y,
      KeyEvent.KEYCODE_J,
      KeyEvent.KEYCODE_I,
        -> {
        viewModel.engine.value?.actionHandler?.onRotateCounterClockwisePressed()
        true
      }

      KeyEvent.KEYCODE_BUTTON_START,
      KeyEvent.KEYCODE_O,
        -> {
        viewModel.engine.value?.actionHandler?.onPausePressed()
        true
      }

      KeyEvent.KEYCODE_BUTTON_R1,
      KeyEvent.KEYCODE_BUTTON_R2,
        -> {
        viewModel.engine.value?.actionHandler?.onHoldPressed()
        true
      }

      else -> {
        super.dispatchKeyEvent(event)
      }
    }
  }
}
