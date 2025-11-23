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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

@Composable
fun FourRoundButtons(
  modifier: Modifier = Modifier,
  buttonSize: Dp,
  onLeftPressed: () -> Unit,
  onRightPressed: () -> Unit,
  onUpPressed: () -> Unit,
  onDownPressed: () -> Unit,
) {
  Box(
    modifier = modifier,
  ) {
    // Left
    RoundButton(
      modifier = Modifier
        .padding(bottom = buttonSize, top = buttonSize)
        .size(buttonSize)
        .pointerInput(Unit) {
          detectTapGestures(
            onPress = {
              onLeftPressed()
            },
          )
        },
    )

    // Down
    RoundButton(
      modifier = Modifier
        .padding(start = buttonSize, top = buttonSize * 2)
        .size(buttonSize)
        .pointerInput(Unit) {
          detectTapGestures(
            onPress = {
              onDownPressed()
            },
          )
        },
    )

    // Up
    RoundButton(
      modifier = Modifier
        .padding(start = buttonSize, bottom = buttonSize * 2)
        .size(buttonSize)
        .pointerInput(Unit) {
          detectTapGestures(
            onPress = {
              onUpPressed()
            },
          )
        },
    )

    // Right
    RoundButton(
      modifier = Modifier
        .padding(start = buttonSize * 2, top = buttonSize)
        .size(buttonSize)
        .pointerInput(Unit) {
          detectTapGestures(
            onPress = {
              onRightPressed()
            },
          )
        },
    )
  }
}

private val buttonColor = Color.White.copy(alpha = .4f)

@Composable
private fun RoundButton(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.drawWithCache {
      onDrawBehind {
        drawCircle(
          color = buttonColor,
        )
      }
    },
  )
}
