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

package com.tunjid.composables.lazy.list

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.lazy.interpolatedIndexOfVisibleItemAt

/**
 * Linearly interpolates the index for the item at [index] in [LazyListLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyListState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param index the index for which its interpolated index in [LazyListLayoutInfo.visibleItemsInfo]
 * should be returned.
 *
 * @param itemIndex a look up for the index for the item in [LazyListLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyListItemInfo.index].
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyListLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyListLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyListLayoutInfo.visibleItemsInfo] does not have an item at [index].
 * */
fun LazyListState.interpolatedIndexOfVisibleItemAt(
    index: Int,
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): Float {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    return interpolatedIndexOfVisibleItemAt(
        lazyState = this,
        visibleItems = visibleItemsInfo,
        index = index,
        itemSize = { it.size },
        offset = { it.offset },
        nextItemOnMainAxis = { visibleItemsInfo.getOrNull(index + 1) },
        itemIndex = itemIndex,
    )
}

/**
 * Linearly interpolates the index for the first item in [LazyListLayoutInfo.visibleItemsInfo]
 * to smoothly match the scroll rate of this [LazyListState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param itemIndex a look up for the index for the item in [LazyListLayoutInfo.visibleItemsInfo].
 * It defaults to [LazyListItemInfo.index].
 *
 * @see [LazyListState.interpolatedIndexOfVisibleItemAt]
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [LazyListLayoutInfo.visibleItemsInfo] or [Float.NaN] if:
 * - [LazyListLayoutInfo.visibleItemsInfo] is empty.
 * - [LazyListLayoutInfo.visibleItemsInfo] does not have an item at the first visible index.
 * */
fun LazyListState.interpolatedFirstItemIndex(
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): Float = interpolatedIndexOfVisibleItemAt(
    index = 0,
    itemIndex = itemIndex,
)