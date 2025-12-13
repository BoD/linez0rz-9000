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
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

class TestActivity : ComponentActivity() {
  private lateinit var myView: MyView

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    myView = MyView(this)
    println("invertAB=$invertAB")
    setContentView(myView)
  }

  @SuppressLint("RestrictedApi")
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.action != KeyEvent.ACTION_DOWN) {
      return super.dispatchKeyEvent(event)
    }

    myView.setBackgroundColor(Color.WHITE)
    myView.postDelayed(
      {
        myView.setBackgroundColor(Color.BLACK)
      },
      16,
    )
    return true

  }
}

private class MyView(context: android.content.Context) : View(context) {
  init {
    isFocusable = true
    isFocusableInTouchMode = true
  }

//  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//    setBackgroundColor(Color.WHITE)
//    postDelayed(
//      {
//        setBackgroundColor(Color.BLACK)
//      },
//      16,
//    )
//    return true
//  }

//  override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
//    setBackgroundColor(if (white) Color.WHITE else Color.BLACK)
//    white = !white
//    return true
//  }

//  override fun onTouchEvent(event: MotionEvent?): Boolean {
//    setBackgroundColor(if (white) Color.WHITE else Color.BLACK)
//    white = !white
//    return true
//  }
}
