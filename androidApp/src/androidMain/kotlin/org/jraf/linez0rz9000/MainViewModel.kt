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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.loadEngine
import org.jraf.linez0rz9000.engine.saveEngineState
import org.jraf.linez0rz9000.engine.storage.Storage

class MainViewModel(application: Application) : AndroidViewModel(application = application) {
  private val storage = Storage(getApplication<Application>().filesDir.resolve("storage.preferences_pb").absolutePath)

  val engine: StateFlow<Engine?> = flow {
    emit(
      storage.loadEngine().also {
        it.start()
        it.pause()
      },
    )
  }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null)

  fun saveEngineState() {
    val engine = engine.value ?: return
    viewModelScope.launch {
      storage.saveEngineState(engine = engine)
    }
  }

  private var isPaused: Boolean = false

  fun pause() {
    isPaused = true
    engine.value?.pause()
  }

  fun resume() {
    if (!isPaused) return
    engine.value?.resume()
    isPaused = false
  }
}