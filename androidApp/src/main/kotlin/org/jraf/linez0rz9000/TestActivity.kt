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
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.os.Trace
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class TestActivity : ComponentActivity() {
  private lateinit var myView: MyView

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    myView = MyView(this)
    setContentView(myView)
  }

  @SuppressLint("RestrictedApi")
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.keyCode != KeyEvent.KEYCODE_DPAD_LEFT && event.keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
      return super.dispatchKeyEvent(event)
    }

    if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
      val eventAgeMs = SystemClock.uptimeMillis() - event.eventTime
      Trace.beginSection("Test key ${KeyEvent.keyCodeToString(event.keyCode)} age=${eventAgeMs}ms")
      try {
        myView.toggle()
      } finally {
        Trace.endSection()
      }
    }
    return true
  }
}

private class MyView(context: android.content.Context) : View(context) {
  private var pressCount = 0

  fun toggle() {
    pressCount++
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    Trace.beginSection("Test draw #$pressCount")
    try {
      canvas.drawColor(if (pressCount % 2 == 0) Color.BLACK else Color.WHITE)
    } finally {
      Trace.endSection()
    }
  }
}
