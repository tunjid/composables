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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.ui.Modifier
import com.tunjid.composables.stickyheader.StickyHeaderLayout

@Composable
inline fun StickyHeaderStaggeredGrid(
    state: LazyStaggeredGridState,
    modifier: Modifier,
    crossinline headerMatcher: @DisallowComposableCalls (LazyStaggeredGridItemInfo) -> Boolean,
    stickyHeader: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    StickyHeaderLayout(
        lazyState = state,
        modifier = modifier,
        viewportStart = { layoutInfo.viewportStartOffset },
        lazyItems = { layoutInfo.visibleItemsInfo },
        lazyItemOffset = { offset.y },
        lazyItemHeight = { size.height },
        headerMatcher = headerMatcher,
        stickyHeader = stickyHeader,
        content = content,
    )
}