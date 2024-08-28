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

package com.tunjid.composables.stickyheader.grid

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tunjid.composables.stickyheader.StickyHeaderLayout

/**
 * Provides a layout that allows for placing a sticky header above the [content] Composable.
 *
 * @param state The [LazyGridState] whose scroll properties will be observed to create a
 * sticky header for.
 * @param modifier The modifier to be applied to the layout.
 * @param itemMutationPolicy the [SnapshotMutationPolicy] for the first item in the grid that
 * the sticky header is currently drawing over. The default implementation is equivalent on
 * [LazyGridItemInfo.key] and [LazyGridItemInfo.contentType].
 * @param isStickyHeaderItem A lambda for identifying which items in the grid are sticky.
 * @param stickyHeader A lambda for drawing the sticky header composable. It also receives the
 * [LazyGridItemInfo] for the item in the grid that the sticky header is currently drawing over.
 * @param content The content the sticky header will be drawn over. This should be a
 * [LazyVerticalGrid].
 */
@Composable
fun StickyHeaderGrid(
    state: LazyGridState,
    modifier: Modifier = Modifier,
    itemMutationPolicy: SnapshotMutationPolicy<LazyGridItemInfo?> = remember {
        object : SnapshotMutationPolicy<LazyGridItemInfo?> {
            override fun equivalent(
                a: LazyGridItemInfo?,
                b: LazyGridItemInfo?
            ): Boolean = a != null && b != null && a.key == b.key && a.contentType == b.contentType
        }
    },
    isStickyHeaderItem: @DisallowComposableCalls (LazyGridItemInfo) -> Boolean,
    stickyHeader: @Composable (LazyGridItemInfo?) -> Unit,
    content: @Composable () -> Unit
) {
    StickyHeaderLayout(
        lazyState = state,
        modifier = modifier,
        itemMutationPolicy = itemMutationPolicy,
        viewportStart = { layoutInfo.viewportStartOffset },
        mainAxisSpacing = { layoutInfo.mainAxisItemSpacing },
        lazyItems = { layoutInfo.visibleItemsInfo },
        lazyItemIndex = { index },
        lazyItemOffset = { offset.y },
        lazyItemHeight = { size.height },
        isStickyHeaderItem = isStickyHeaderItem,
        stickyHeader = stickyHeader,
        content = content,
    )
}