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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine

@Composable
@Preview
fun App(engine: Engine) {
  MaterialTheme {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      val board: Board by engine.board.collectAsState()
      val state: Engine.State by engine.state.collectAsState()
      Board(board, state)

      when (state) {
        is Engine.State.Running -> {
          // Nothing
        }

        is Engine.State.Paused, is Engine.State.GameOver -> {
          Box(Modifier.fillMaxSize()) {
            Button(
              modifier = Modifier.align(Alignment.Center),
              onClick = {
                when (state) {
                  is Engine.State.Paused -> engine.unpause()
                  is Engine.State.GameOver -> engine.restart()
                  else -> throw IllegalStateException()
                }
              },
            ) {
              Text(
                when (state) {
                  is Engine.State.Paused -> "Resume"
                  is Engine.State.GameOver -> "Play again"
                  else -> throw IllegalStateException()
                },
              )
            }
          }
        }
      }
    }
  }
}

