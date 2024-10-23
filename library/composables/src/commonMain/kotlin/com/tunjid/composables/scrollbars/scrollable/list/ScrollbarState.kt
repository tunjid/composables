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

package com.tunjid.composables.scrollbars.scrollable.list

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.lazy.itemVisibilityPercentage
import com.tunjid.composables.lazy.list.interpolatedFirstItemIndex
import com.tunjid.composables.scrollbars.Scrollbar
import com.tunjid.composables.scrollbars.ScrollbarState
import com.tunjid.composables.scrollbars.scrollable.rememberScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.sumOf
import com.tunjid.composables.scrollbars.scrollbarStateValue
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.min

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a [LazyListState]
 * based on the total items in the list, and [LazyListState.scrollToItem] for responding to
 * scrollbar thumb displacements.
 *
 * For more customization, including animated scrolling @see [rememberScrollbarThumbMover].
 */
@Composable
inline fun LazyListState.rememberBasicScrollbarThumbMover(): (Float) -> Unit {
    var totalItemsCount by remember { mutableStateOf(0) }
    LaunchedEffect(this) {
        snapshotFlow { layoutInfo.totalItemsCount }
            .collect { totalItemsCount = it }
    }
    return rememberScrollbarThumbMover(
        itemsAvailable = totalItemsCount,
        scroll = ::scrollToItem,
    )
}

/**
 * Calculates a [ScrollbarState] driven by the changes in a [LazyListState].
 *
 * The calculations for [ScrollbarState] assumes homogeneous items. For heterogeneous items,
 * the produced state may not change smoothly. If this is the case, you may derive your own
 * [ScrollbarState] using an algorithm that better fits your list items.
 *
 * @param itemsAvailable the total amount of items available to scroll in the lazy list.
 * @param itemIndex a lookup function for index of an item in the list relative to [itemsAvailable].
 *
 * @sample com.tunjid.demo.common.app.demos.LazyListDemoScreen
 */
@Composable
fun LazyListState.scrollbarState(
    itemsAvailable: Int,
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): ScrollbarState {
    val state = remember { ScrollbarState() }
    LaunchedEffect(this, itemsAvailable) {
        snapshotFlow {
            if (itemsAvailable == 0) return@snapshotFlow null

            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@snapshotFlow null

            val firstIndex = min(
                a = interpolatedFirstItemIndex(itemIndex),
                b = itemsAvailable.toFloat(),
            )
            if (firstIndex.isNaN()) return@snapshotFlow null

            val itemsVisible = visibleItemsInfo.sumOf { itemInfo ->
                itemVisibilityPercentage(
                    itemSize = itemInfo.size,
                    itemStartOffset = itemInfo.offset,
                    viewportStartOffset = layoutInfo.viewportStartOffset,
                    viewportEndOffset = layoutInfo.viewportEndOffset,
                )
            }

            val thumbTravelPercent = min(
                a = firstIndex / itemsAvailable,
                b = 1f,
            )
            val thumbSizePercent = min(
                a = itemsVisible / itemsAvailable,
                b = 1f,
            )
            scrollbarStateValue(
                thumbSizePercent = thumbSizePercent,
                thumbMovedPercent = when {
                    layoutInfo.reverseLayout -> 1f - thumbTravelPercent
                    else -> thumbTravelPercent
                },
            )
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { state.onScroll(it) }
    }
    return state
}