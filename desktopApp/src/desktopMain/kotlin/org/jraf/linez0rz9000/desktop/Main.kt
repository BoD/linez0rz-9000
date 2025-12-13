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

package org.jraf.linez0rz9000.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import org.jraf.linez0rz9000.engine.loadEngine
import org.jraf.linez0rz9000.engine.saveEngineState
import org.jraf.linez0rz9000.engine.storage.Storage
import org.jraf.linez0rz9000.ui.App
import org.jraf.linez0rz9000.ui.KeyHandler

fun main() {
  val storage = Storage("${System.getProperty("user.home")}/.linez0rz9000/storage.preferences_pb")
  val engine = runBlocking { storage.loadEngine() }
  engine.start()
  application {
    val gameLineCountTo9000: Int by engine.gameLineCountTo9000.collectAsState()
    Window(
      onCloseRequest = {
        runBlocking {
          storage.saveEngineState(engine = engine)
        }
        exitApplication()
      },
      title = "Linez0rz $gameLineCountTo9000",
    ) {
      LaunchedEffect(LocalWindowInfo.current.isWindowFocused) {
        storage.saveEngineState(engine = engine)
      }

      Box(
        modifier = Modifier
          .safeContentPadding()
          .fillMaxSize(),
      ) {
        App(engine)
        KeyHandler(
          engine = engine,
          gamepadMode = false,
          gamepadInvertAB = false,
        )
      }
    }
  }
}
