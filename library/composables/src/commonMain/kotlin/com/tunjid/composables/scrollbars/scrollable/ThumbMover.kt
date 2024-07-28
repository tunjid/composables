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

package com.tunjid.composables.scrollbars.scrollable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.tunjid.composables.scrollbars.ScrollbarState
import kotlin.math.roundToInt

/**
 * Generic function to react to scrollbar thumb movements for a [ScrollbarState].
 * @param itemsAvailable the total amount of items available to scroll in the layout.
 * @param scroll a function to be invoked when an index has been identified to scroll to.
 */
@Composable
inline fun rememberScrollbarThumbMover(
    itemsAvailable: Int,
    crossinline scroll: suspend (index: Int) -> Unit,
): (Float) -> Unit {
    var percentage by remember { mutableFloatStateOf(Float.NaN) }
    val itemCount by rememberUpdatedState(itemsAvailable)

    LaunchedEffect(percentage) {
        if (percentage.isNaN()) return@LaunchedEffect
        val indexToFind = (itemCount * percentage).roundToInt()
        scroll(indexToFind)
    }
    return remember {
        { newPercentage -> percentage = newPercentage }
    }
}

inline fun <T> List<T>.sumOf(selector: (T) -> Float): Float =
    fold(initial = 0f) { accumulator, listItem -> accumulator + selector(listItem) }