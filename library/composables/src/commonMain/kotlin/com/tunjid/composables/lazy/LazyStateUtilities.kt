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

package com.tunjid.composables.lazy

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlin.math.abs

/**
 * Remembers [LazyState] by saving two [Int] values from it via [rememberSaveable]:
 *
 * @param firstVisibleItemIndex The first visible item index in the LazyState
 * and allows for modifying these values upon restoration.
 * @param firstVisibleItemScrollOffset The scroll offset of the first visible item.
 *
 * Typically this is used to update the [firstVisibleItemIndex] or [firstVisibleItemScrollOffset]
 * when the lazy state is restored.
 *
 * For example, consider an item that is scrolled to, and interacting with it causes the
 * item composed to leave the composition. An [MutableIntState] can be written to with
 * the item's current position, such that when returned to and the scrollable container is
 * recomposed, the item interacted with can be recomposed with it firmly in focus
 * as defined by the value written into a previously saved [MutableIntState].
 */
@Composable
inline fun <LazyState : ScrollableState> rememberLazyScrollableState(
    noinline init: () -> LazyState,
    crossinline firstVisibleItemIndex: LazyState.() -> Int,
    crossinline firstVisibleItemScrollOffset: LazyState.() -> Int,
    crossinline restore: (firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) -> LazyState,
): LazyState = rememberSaveable(
    saver = listSaver(
        save = { lazyScrollableState ->
            listOf(
                lazyScrollableState.firstVisibleItemIndex(),
                lazyScrollableState.firstVisibleItemScrollOffset(),
            )
        },
        restore = { (firstVisibleItemIndex, firstVisibleItemScrollOffset) ->
            restore(firstVisibleItemIndex, firstVisibleItemScrollOffset)
        },
    ),
    init = init,
)

/**
 * Linearly interpolates the index for the first item in [visibleItems] to smoothly match the
 * scroll rate of the backing [ScrollableState].
 *
 * @param visibleItems a list of items currently visible in the layout.
 * @param itemSize a lookup function for the size of an item in the layout.
 * @param offset a lookup function for the offset of an item relative to the start of the view port.
 * @param nextItemOnMainAxis a lookup function for the next item on the main axis in the direction
 * of the scroll.
 * @param itemIndex a lookup function for index of an item in the layout relative to
 * the total amount of items available.
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition) where nextItemPosition
 * is the index of the consecutive item along the major axis.
 * */
inline fun <LazyState : ScrollableState, LazyStateItem> LazyState.interpolatedFirstItemIndex(
    visibleItems: List<LazyStateItem>,
    crossinline itemSize: LazyState.(LazyStateItem) -> Int,
    crossinline offset: LazyState.(LazyStateItem) -> Int,
    crossinline nextItemOnMainAxis: LazyState.(LazyStateItem) -> LazyStateItem?,
    crossinline itemIndex: (LazyStateItem) -> Int,
): Float {
    if (visibleItems.isEmpty()) return 0f

    val firstItem = visibleItems.first()
    val firstItemIndex = itemIndex(firstItem)

    if (firstItemIndex < 0) return Float.NaN

    val firstItemSize = itemSize(firstItem)
    if (firstItemSize == 0) return Float.NaN

    val itemOffset = offset(firstItem).toFloat()
    val offsetPercentage = abs(itemOffset) / firstItemSize

    val nextItem = nextItemOnMainAxis(firstItem) ?: return firstItemIndex + offsetPercentage

    val nextItemIndex = itemIndex(nextItem)

    return firstItemIndex + ((nextItemIndex - firstItemIndex) * offsetPercentage)
}

/**
 * Linearly interpolates the index for the item at [index] in [visibleItems] to smoothly match the
 * scroll rate of the backing [ScrollableState].
 *
 * This method should not be read in composition as it changes frequently with scroll state.
 * Instead it should be read in an in effect block inside of a [snapshotFlow].
 *
 * @param visibleItems a list of items currently visible in the layout.
 * @param itemSize a lookup function for the size of an item in the layout.
 * @param offset a lookup function for the offset of an item relative to the start of the view port.
 * @param nextItemOnMainAxis a lookup function for the next item on the main axis in the direction
 * of the scroll.
 * @param itemIndex a lookup function for index of an item in the layout relative to
 * the total amount of items available.
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition) or [Float.NaN] if:
 * - [visibleItems] returns an empty [List].
 * - [visibleItems] does not have an item at [index].
 * */
internal inline fun <LazyState : ScrollableState, LazyStateItem> interpolatedIndexOfVisibleItemAt(
    lazyState: LazyState,
    visibleItems: List<LazyStateItem>,
    index: Int,
    crossinline itemSize: LazyState.(LazyStateItem) -> Int,
    crossinline offset: LazyState.(LazyStateItem) -> Int,
    crossinline nextItemOnMainAxis: LazyState.(LazyStateItem) -> LazyStateItem?,
    crossinline itemIndex: (LazyStateItem) -> Int,
): Float {
    if (visibleItems.isEmpty()) return Float.NaN

    val item = visibleItems.getOrNull(index) ?: return Float.NaN
    val firstItemIndex = itemIndex(item)

    if (firstItemIndex < 0) return Float.NaN

    val firstItemSize = lazyState.itemSize(item)
    if (firstItemSize == 0) return Float.NaN

    val itemOffset = lazyState.offset(item).toFloat()
    val offsetPercentage = abs(itemOffset) / firstItemSize

    val nextItem = lazyState.nextItemOnMainAxis(item) ?: return firstItemIndex + offsetPercentage

    val nextItemIndex = itemIndex(nextItem)

    return firstItemIndex + ((nextItemIndex - firstItemIndex) * offsetPercentage)
}

/**
 * Returns the percentage of an item that is currently visible in the view port.
 * @param itemSize the size of the item
 * @param itemStartOffset the start offset of the item relative to the view port start
 * @param viewportStartOffset the start offset of the view port
 * @param viewportEndOffset the end offset of the view port
 */
internal fun itemVisibilityPercentage(
    itemSize: Int,
    itemStartOffset: Int,
    viewportStartOffset: Int,
    viewportEndOffset: Int,
): Float {
    if (itemSize == 0) return 0f
    val itemEnd = itemStartOffset + itemSize
    val startOffset = when {
        itemStartOffset > viewportStartOffset -> 0
        else -> abs(abs(viewportStartOffset) - abs(itemStartOffset))
    }
    val endOffset = when {
        itemEnd < viewportEndOffset -> 0
        else -> abs(abs(itemEnd) - abs(viewportEndOffset))
    }
    val size = itemSize.toFloat()
    return (size - startOffset - endOffset) / size
}
