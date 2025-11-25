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

package org.jraf.linez0rz9000.engine.util

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/*
 * Taken from https://github.com/Kotlin/kotlinx.coroutines/issues/2631#issuecomment-2812699291
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
fun <T, R> StateFlow<T>.mapState(transform: (T) -> R): StateFlow<R> = object : StateFlow<R> {
  override val replayCache: List<R> get() = listOf(value)

  override suspend fun collect(collector: FlowCollector<R>): Nothing {
    var lastEmittedValue: Any? = nullSurrogate
    this@mapState.collect { newValue ->
      val transformedValue = transform(newValue)
      if (transformedValue != lastEmittedValue) {
        lastEmittedValue = transformedValue
        collector.emit(transformedValue)
      }
    }
  }

  private var lastUpstreamValue = this@mapState.value

  override var value: R = transform(lastUpstreamValue)
    private set
    get() {
      val currentUpstreamValue: T = this@mapState.value
      if (currentUpstreamValue == lastUpstreamValue) return field
      val newValue = transform(currentUpstreamValue)
      field = newValue
      lastUpstreamValue = currentUpstreamValue
      return newValue
    }
}

private val nullSurrogate = Any()
