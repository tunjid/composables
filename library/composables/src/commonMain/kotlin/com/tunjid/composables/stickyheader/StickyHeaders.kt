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

package com.tunjid.composables.stickyheader


import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.IntOffset

@Composable
internal inline fun <LazyState : ScrollableState, LazyItem> StickyHeaderLayout(
    lazyState: LazyState,
    modifier: Modifier = Modifier,
    itemMutationPolicy: SnapshotMutationPolicy<LazyItem?>,
    crossinline viewportStart: @DisallowComposableCalls LazyState.() -> Int,
    crossinline mainAxisSpacing: @DisallowComposableCalls LazyState.() -> Int,
    crossinline lazyItems: @DisallowComposableCalls LazyState.() -> List<LazyItem>,
    crossinline lazyItemIndex: @DisallowComposableCalls LazyItem.() -> Int,
    crossinline lazyItemOffset: @DisallowComposableCalls LazyItem.() -> Int,
    crossinline lazyItemHeight: @DisallowComposableCalls LazyItem.() -> Int,
    crossinline isStickyHeaderItem: @DisallowComposableCalls LazyItem.() -> Boolean,
    stickyHeader: @Composable (LazyItem?) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.clipToBounds()) {
        content()
        var headerOffset by remember { mutableIntStateOf(Int.MIN_VALUE) }
        LaunchedEffect(lazyState) {
            snapshotFlow {
                val startOffset = lazyState.viewportStart()
                val visibleItems = lazyState.lazyItems()
                val firstItem = visibleItems.firstOrNull() ?: return@snapshotFlow Int.MIN_VALUE

                val firstItemIndex = firstItem.lazyItemIndex()
                val firstItemOffset = firstItem.lazyItemOffset()

                // The first item hast scrolled to the top of the view port yet, show nothing
                if (firstItemIndex == 0 && firstItemOffset > startOffset) return@snapshotFlow Int.MIN_VALUE

                val firstCompletelyVisibleItem = visibleItems.firstOrNull { lazyItem ->
                    lazyItemOffset(lazyItem) >= startOffset
                } ?: return@snapshotFlow Int.MIN_VALUE

                when (isStickyHeaderItem(firstCompletelyVisibleItem)) {
                    false -> 0
                    true -> firstCompletelyVisibleItem.lazyItemHeight()
                        .minus(if (firstItemIndex == 0) 0 else lazyState.mainAxisSpacing())
                        .minus(firstCompletelyVisibleItem.lazyItemOffset())
                        .let { difference -> if (difference < 0) 0 else -difference }
                }
            }
                .collect { headerOffset = it }
        }
        val canShowStickyHeader by remember {
            derivedStateOf(
                policy = structuralEqualityPolicy(),
                calculation = { headerOffset > Int.MIN_VALUE }
            )
        }
        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = 0,
                    y = headerOffset
                )
            }
        ) {
            val firstVisibleItem by remember {
                derivedStateOf(
                    policy = itemMutationPolicy,
                    calculation = { lazyState.lazyItems().firstOrNull() }
                )
            }
            if (canShowStickyHeader) stickyHeader(firstVisibleItem)
        }
    }
}
