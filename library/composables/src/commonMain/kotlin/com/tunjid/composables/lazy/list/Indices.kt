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
import androidx.compose.foundation.lazy.LazyListState
import com.tunjid.composables.lazy.interpolatedFirstItemIndex

/**
 * Linearly interpolates the index for the first item to smoothly match the
 * scroll rate in [this].
 *
 * Note that the value returned is observable and is updated after every scroll or remeasure.
 * If you use it in the composable function it will be recomposed on every change causing
 * potential performance issues including infinity recomposition loop.
 * Therefore, avoid using it in the composition.
 * */
fun LazyListState.interpolatedFirstItemIndex(
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): Float {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    return interpolatedFirstItemIndex(
        visibleItems = visibleItemsInfo,
        itemSize = { it.size },
        offset = { it.offset },
        nextItemOnMainAxis = { visibleItemsInfo.getOrNull(1) },
        itemIndex = itemIndex,
    )
}