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
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.valueOf

/**
 * Linearly interpolates the index for the item at [index] in [LazyGridLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyGridState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param index the index for which its interpolated index in [LazyGridLayoutInfo.visibleItemsInfo]
 * should be returned.
 *
 * @param itemIndex a look up for the index for the item in [LazyGridLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyGridItemInfo.index].
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyGridLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyGridLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyGridLayoutInfo.visibleItemsInfo] does not have an item at [index].
 * */
fun LazyGridState.interpolatedIndexOfVisibleItemAt(
    index: Int,
    itemIndex: (LazyGridItemInfo) -> Int = LazyGridItemInfo::index,
): Float {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    return com.tunjid.composables.lazy.interpolatedIndexOfVisibleItemAt(
        lazyState = this,
        visibleItems = visibleItemsInfo,
        index = index,
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

/**
 * Linearly interpolates the index for the first item in [LazyGridLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyGridState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param itemIndex a look up for the index for the item in [LazyGridLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyGridItemInfo.index].
 *
 * @see [LazyGridState.interpolatedIndexOfVisibleItemAt]
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyGridLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyGridLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyGridLayoutInfo.visibleItemsInfo] does not have an item at the first visible index.
 * */
fun LazyGridState.interpolatedFirstItemIndex(
    itemIndex: (LazyGridItemInfo) -> Int = LazyGridItemInfo::index,
): Float = interpolatedIndexOfVisibleItemAt(
    index = 0,
    itemIndex = itemIndex,
)