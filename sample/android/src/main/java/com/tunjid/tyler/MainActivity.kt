/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tunjid.tyler

import android.os.Bundle
import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import com.tunjid.demo.common.ui.App
import com.tunjid.demo.common.ui.AppState
import com.tunjid.demo.common.ui.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val appState = remember { AppState() }
                App(appState)

                PredictiveBackHandler { backEvents: Flow<BackEventCompat> ->
                    try {
                        backEvents.collect { backEvent ->
                            appState.backPreviewState.apply {
                                atStart = backEvent.swipeEdge == BackEventCompat.EDGE_LEFT
                                progress = backEvent.progress
                                pointerOffset = Offset(
                                    x = backEvent.touchX,
                                    y = backEvent.touchY
                                ).round()
                            }
                        }
                        // Dismiss back preview
                        appState.backPreviewState.apply {
                            progress = Float.NaN
                            pointerOffset = IntOffset.Zero
                        }
                        // Pop navigation
                        appState.goBack()
                    } catch (e: CancellationException) {
                        appState.backPreviewState.apply {
                            progress = Float.NaN
                            pointerOffset = IntOffset.Zero
                        }
                    }
                }
            }
        }
    }
}
