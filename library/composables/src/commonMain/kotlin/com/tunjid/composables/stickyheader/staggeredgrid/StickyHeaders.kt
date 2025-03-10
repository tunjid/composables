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

package com.tunjid.composables.stickyheader.staggeredgrid

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tunjid.composables.stickyheader.StickyHeaderLayout

/**
 * Provides a layout that allows for placing a sticky header above the [content] Composable.
 *
 * @param state The [LazyStaggeredGridState] whose scroll properties will be observed to create a
 * sticky header for.
 * @param modifier The modifier to be applied to the layout.
 * [LazyStaggeredGridItemInfo.key] and [LazyStaggeredGridItemInfo.contentType].
 * @param isStickyHeaderItem A lambda for identifying which items in the staggered grid are sticky.
 * @param stickyHeader A lambda for drawing the sticky header composable. It also receives the
 * [LazyStaggeredGridItemInfo.key] and [LazyStaggeredGridItemInfo.contentType] for
 * the item in the grid that the sticky header is currently drawing over.
 * @param content The content the sticky header will be drawn over. This should be a
 * [LazyVerticalStaggeredGrid].
 *
 * @sample com.tunjid.demo.common.app.demos.LazyStickyHeaderStaggeredGridDemoScreen
 *
 */
@Composable
fun StickyHeaderStaggeredGrid(
    state: LazyStaggeredGridState,
    modifier: Modifier = Modifier,
    isStickyHeaderItem: @DisallowComposableCalls (LazyStaggeredGridItemInfo) -> Boolean,
    stickyHeader: @Composable (index: Int, key: Any?, contentType: Any?) -> Unit,
    content: @Composable () -> Unit
) {
    StickyHeaderLayout(
        lazyState = state,
        modifier = modifier,
        itemMutationPolicy = remember {
            object : SnapshotMutationPolicy<LazyStaggeredGridItemInfo?> {
                override fun equivalent(
                    a: LazyStaggeredGridItemInfo?,
                    b: LazyStaggeredGridItemInfo?
                ): Boolean =
                    a != null && b != null &&
                            a.key == b.key &&
                            a.contentType == b.contentType &&
                            a.index == b.index
            }
        },
        viewportStart = { layoutInfo.viewportStartOffset },
        lazyItems = { layoutInfo.visibleItemsInfo },
        lazyItemIndex = { index },
        lazyItemOffset = { offset.y },
        lazyItemHeight = { size.height },
        isStickyHeaderItem = isStickyHeaderItem,
        stickyHeader = { itemInfo ->
            if (itemInfo != null) stickyHeader(
                itemInfo.index,
                itemInfo.key,
                itemInfo.contentType
            )
        },
        content = content,
    )
}