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

package com.tunjid.composables.lazy.staggeredgrid

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.valueOf

/**
 * Linearly interpolates the index for the item at [index] in [LazyStaggeredGridLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyStaggeredGridState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param index the index for which its interpolated index in [LazyStaggeredGridLayoutInfo.visibleItemsInfo]
 * should be returned.
 *
 * @param itemIndex a look up for the index for the item in [LazyStaggeredGridLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyStaggeredGridItemInfo.index].
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyStaggeredGridLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyStaggeredGridLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyStaggeredGridLayoutInfo.visibleItemsInfo] does not have an item at [index].
 * */
fun LazyStaggeredGridState.interpolatedIndexOfVisibleItemAt(
    index: Int,
    itemIndex: (LazyStaggeredGridItemInfo) -> Int = LazyStaggeredGridItemInfo::index,
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
                if (next.lane == first.lane) return@nextItem next
            }
            null
        },
        itemIndex = itemIndex,
    )
}

/**
 * Linearly interpolates the index for the first item in [LazyStaggeredGridLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyStaggeredGridState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param itemIndex a look up for the index for the item in [LazyStaggeredGridLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyStaggeredGridItemInfo.index].
 *
 * @see [LazyStaggeredGridState.interpolatedIndexOfVisibleItemAt]
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyStaggeredGridLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyStaggeredGridLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyStaggeredGridLayoutInfo.visibleItemsInfo] does not have an item at the first visible index.
 * */
fun LazyStaggeredGridState.interpolatedFirstItemIndex(
    itemIndex: (LazyStaggeredGridItemInfo) -> Int = LazyStaggeredGridItemInfo::index,
): Float = interpolatedIndexOfVisibleItemAt(
    index = 0,
    itemIndex = itemIndex,
)