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

package com.tunjid.composables.lazy.grid

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.LaunchedEffect
import com.tunjid.composables.lazy.interpolatedFirstItemIndex
import com.tunjid.composables.valueOf

/**
 * Linearly interpolates the index for the first item to smoothly match the
 * scroll rate in [this].
 *
 * Note that the value returned is observable and is updated after every scroll or remeasure.
 * If you use it in the composable function it will be recomposed on every change causing
 * potential performance issues including infinity recomposition loop.
 * Therefore, avoid using it in the composition and instead in a [LaunchedEffect].
 * */
fun LazyGridState.interpolatedFirstItemIndex(
    itemIndex: (LazyGridItemInfo) -> Int = LazyGridItemInfo::index,
): Float {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    return interpolatedFirstItemIndex(
        visibleItems = visibleItemsInfo,
        itemSize = { layoutInfo.orientation.valueOf(it.size) },
        offset = { layoutInfo.orientation.valueOf(it.offset) },
        nextItemOnMainAxis = nextItem@{ first ->
            for (i in 1..visibleItemsInfo.lastIndex) {
                val next = visibleItemsInfo[i]
                val found = when (layoutInfo.orientation) {
                    Orientation.Vertical -> next.row != first.row
                    Orientation.Horizontal -> next.column != first.column
                }
                if (found) return@nextItem next
            }
            null
        },
        itemIndex = itemIndex,
    )
}