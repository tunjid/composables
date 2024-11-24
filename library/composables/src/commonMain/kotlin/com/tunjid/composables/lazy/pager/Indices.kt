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

package com.tunjid.composables.lazy.pager

import androidx.compose.foundation.pager.PageInfo
import androidx.compose.foundation.pager.PagerLayoutInfo
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.snapshotFlow
import com.tunjid.composables.lazy.interpolatedIndexOfVisibleItemAt

/**
 * Linearly interpolates the index for the item at [index] in [PagerLayoutInfo.visiblePagesInfo]
 * to smoothly match the scroll rate of this [PagerState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param index the index for which its interpolated index in [PagerLayoutInfo.visiblePagesInfo]
 * should be returned.
 *
 * @param itemIndex a look up for the index for the item in [PagerLayoutInfo.visiblePagesInfo].
 * It defaults to [PageInfo.index].
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [PagerLayoutInfo.visiblePagesInfo] or [Float.NaN] if:
 * - [PagerLayoutInfo.visiblePagesInfo] is empty.
 * - [PagerLayoutInfo.visiblePagesInfo] does not have an item at [index].
 * */
fun PagerState.interpolatedIndexOfVisibleItemAt(
    index: Int,
    itemIndex: (PageInfo) -> Int = PageInfo::index,
): Float {
    val visiblePagesInfo = layoutInfo.visiblePagesInfo
    return interpolatedIndexOfVisibleItemAt(
        lazyState = this,
        visibleItems = visiblePagesInfo,
        index = index,
        itemSize = { layoutInfo.pageSize },
        offset = { it.offset },
        nextItemOnMainAxis = { visiblePagesInfo.getOrNull(index + 1) },
        itemIndex = itemIndex,
    )
}

/**
 * Linearly interpolates the index for the first item in [PagerLayoutInfo.visiblePagesInfo]
 * to smoothly match the scroll rate of this [PagerState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param itemIndex a look up for the index for the item in [PagerLayoutInfo.visiblePagesInfo].
 * It defaults to [PageInfo.index].
 *
 * @see [PagerState.interpolatedIndexOfVisibleItemAt]
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition)
 * in [PagerLayoutInfo.visiblePagesInfo] or [Float.NaN] if:
 * - [PagerLayoutInfo.visiblePagesInfo] is empty.
 * - [PagerLayoutInfo.visiblePagesInfo] does not have an item at the first visible index.
 * */
fun PagerState.interpolatedFirstItemIndex(
    itemIndex: (PageInfo) -> Int = PageInfo::index,
): Float = interpolatedIndexOfVisibleItemAt(
    index = 0,
    itemIndex = itemIndex,
)
