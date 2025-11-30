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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import linez0rz_9000.ui.generated.resources.Res
import linez0rz_9000.ui.generated.resources.Workbench
import org.jetbrains.compose.resources.Font
import org.jraf.linez0rz9000.engine.Board
import org.jraf.linez0rz9000.engine.Engine
import org.jraf.linez0rz9000.engine.Piece
import kotlin.time.Duration.Companion.seconds

@Composable
fun App(engine: Engine) {
  val workbenchFontFamily = FontFamily(Font(Res.font.Workbench, FontWeight.Normal))

  val board: Board by engine.board.collectAsState()
  val state: Engine.State by engine.state.collectAsState()
  val nextPieces: List<Piece> by engine.nextPieces.collectAsState()
  val heldPiece: Engine.PieceWithPosition? by engine.heldPiece.collectAsState()
  val sessionLineCount: Int by engine.sessionLineCount.collectAsState()
  val gameLineCount: Int by engine.gameLineCount.collectAsState()
  val gameLineCountTo9000: Int by engine.gameLineCountTo9000.collectAsState()
  val gameLineCountMax: Int by engine.gameLineCountMax.collectAsState()

  val scope = rememberCoroutineScope()
  var buttonsVisible by remember { mutableStateOf(true) }
  var hideButtonsJob by remember { mutableStateOf<Job?>(null) }

  val showButtons = {
    buttonsVisible = true
    hideButtonsJob?.cancel()
    hideButtonsJob = scope.launch {
      delay(5.seconds)
      buttonsVisible = false
    }
  }

  Box(
    Modifier
      .fillMaxSize()
      .background(Color.DarkGray)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            showButtons()
          },
        )
      },
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize(),
      horizontalArrangement = Arrangement.Center,
    ) {
      Box(
        modifier = Modifier.fillMaxHeight().weight(1f, fill = false),
        contentAlignment = Alignment.Center,
      ) {
        Board(board = board, state = state, backgroundOnly = true)

        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Spacer(Modifier.weight(.25F))
          Text(
            color = countdownTextColor,
            text = "${if (gameLineCountTo9000 > 0) gameLineCountTo9000 else "0!!!"}",
            autoSize = TextAutoSize.StepBased(),
            softWrap = false,
            fontFamily = workbenchFontFamily,
          )
          Spacer(Modifier.weight(1F))
        }

        Board(board = board, state = state)
      }

      Column(
        modifier = Modifier.width(64.dp).padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.size(8.dp))

        // Game
        Text(
          color = pieceColor(state),
          text = "Game",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$gameLineCount",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(16.dp))

        // Session
        Text(
          color = pieceColor(state),
          text = "Sess",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$sessionLineCount",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Best
        Text(
          color = pieceColor(state),
          text = "Best",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          color = debrisColor(state),
          text = "$gameLineCountMax",
          autoSize = TextAutoSize.StepBased(),
          softWrap = false,
          fontFamily = workbenchFontFamily,
        )

        // Held piece
        if (heldPiece != null) {
          Spacer(modifier = Modifier.size(16.dp))
          Piece(
            piece = heldPiece!!.piece,
            color = pieceColor(state),
          )
          Spacer(modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.weight(1F))

        // Next pieces
        NextPieces(
          nextPieces = nextPieces,
          state = state,
        )

        Spacer(modifier = Modifier.weight(1F))
      }
    }

    if (buttonsVisible) {
      FourRoundButtons(
        modifier = Modifier
          .padding(start = 16.dp, bottom = 160.dp)
          .align(Alignment.BottomStart),
        buttonSize = 36.dp,
        onLeftPressed = { showButtons(); engine.actionHandler.onLeftPressed() },
        onRightPressed = { showButtons(); engine.actionHandler.onRightPressed() },
        onUpPressed = { showButtons(); engine.actionHandler.onDropPressed() },
        onDownPressed = { showButtons(); engine.actionHandler.onDownPressed() },
      )

      FourRoundButtons(
        modifier = Modifier
          .padding(end = 16.dp, bottom = 160.dp)
          .align(Alignment.BottomEnd),
        buttonSize = 36.dp,
        onLeftPressed = { showButtons(); engine.actionHandler.onHoldPressed() },
        onRightPressed = { showButtons(); engine.actionHandler.onRotateClockwisePressed() },
        onUpPressed = { showButtons(); engine.actionHandler.onPausePressed() },
        onDownPressed = { showButtons(); engine.actionHandler.onRotateCounterClockwisePressed() },
      )
    }

    LaunchedEffect(Unit) {
      delay(5.seconds)
      buttonsVisible = false
    }

    GameControlsPanel(engine = engine, state = state)
  }
}

@Composable
private fun GameControlsPanel(
  engine: Engine,
  state: Engine.State,
) {
  when (state) {
    is Engine.State.Running -> {
      // Nothing
    }

    is Engine.State.Paused,
    is Engine.State.GameOver,
      -> {
      Box(Modifier.fillMaxSize()) {
        Button(
          modifier = Modifier.align(Alignment.Center),
          onClick = {
            when (state) {
              is Engine.State.Paused -> engine.resume()
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

