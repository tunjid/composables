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

package com.tunjid.composables.scrollbars.scrollable.scroll


import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.scrollbars.Scrollbar
import com.tunjid.composables.scrollbars.ScrollbarState
import com.tunjid.composables.scrollbars.scrollbarStateValue
import kotlin.math.roundToInt

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a
 * [ScrollState]
 */
@Composable
fun ScrollState.rememberScrollbarThumbMover(): (Float) -> Unit {
    var percentage by remember { mutableFloatStateOf(Float.NaN) }

    LaunchedEffect(percentage) {
        if (percentage.isNaN()) return@LaunchedEffect
        scrollTo((maxValue * percentage).roundToInt())
    }
    return remember {
        { newPercentage -> percentage = newPercentage }
    }
}

/**
 * Remembers a [ScrollbarState] driven by the changes in a [ScrollbarState].
 */
@Composable
fun ScrollState.scrollbarState(): ScrollbarState {
    val state = remember { ScrollbarState() }
    LaunchedEffect(this) {
        snapshotFlow {
            scrollbarStateValue(
                thumbSizePercent = viewportSize.toFloat() / maxValue,
                thumbMovedPercent = value.toFloat() / maxValue,
            )
        }
            .collect { state.onScroll(it) }
    }
    return state
}
