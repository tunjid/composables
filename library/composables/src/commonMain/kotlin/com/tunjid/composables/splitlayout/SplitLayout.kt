/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tunjid.composables.splitlayout

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.unit.toSize
import com.tunjid.composables.valueOf

/**
 * State describing the behavior for [SplitLayout].
 *
 * @param orientation The orientation of the layout.
 * @param maxCount The maximum number of children in the layout.
 * @param initialVisibleCount The initial amount of children visible in the layout.
 * @param minSize The minimum size of a child in the layout.
 */
@Stable
class SplitLayoutState(
    val orientation: Orientation,
    val maxCount: Int,
    initialVisibleCount: Int = maxCount,
    minSize: Dp = 80.dp,
) {

    var visibleCount by mutableIntStateOf(initialVisibleCount)
    var minSize by mutableStateOf(minSize)
    var size by mutableStateOf(orientation.valueOf(DpSize.Zero))
        internal set

    private val weightSum by derivedStateOf {
        checkVisibleCount()
        (0..<visibleCount).sumOf { weightAt(it).toDouble() }.toFloat()
    }

    private val offsetLookup by derivedStateOf {
        (0..<visibleCount).map { index ->
            val previousIndexOffset =
                if (index == 0) 0.dp
                else (weightAt(index - 1) / weightSum) * size
            val indexOffset = (weightAt(index) / weightSum) * size
            previousIndexOffset + indexOffset
        }
    }

    private val weightMap = mutableStateMapOf<Int, Float>().apply {
        (0..<maxCount).forEach { index -> put(index, 1f / maxCount) }
    }

    init {
        checkVisibleCount()
    }

    /**
     * Returns the weight of the child at the specified index.
     * @param index The index whose weight should be returned.
     */
    fun weightAt(index: Int): Float = weightMap.getValue(index)

    /**
     * Attempts to set the weight at [index] and returns the status of the attempt. Reasons
     * for failure include:
     * - Negative weights
     * - Weights that would violate [minSize].
     * - Weights that are greater than 1/maxCount
     *
     * @param index The index to set the weight at.
     * @param weight The weight of this index relative to the weight sum of all the layout. It
     * should be less that 1/maxCount.
     */
    fun setWeightAt(index: Int, weight: Float): Boolean {
        if (weight <= 0f) return false
        if (weight * size < minSize) return false

        val oldWeight = weightMap.getValue(index)
        val weightDifference = oldWeight - weight
        val adjustedIndex = (0..<maxCount).firstNotNullOfOrNull search@{ i ->
            val searchIndex = (index + i) % maxCount
            if (searchIndex == index) return@search null

            val adjustedWidth = (weightMap.getValue(searchIndex) + weightDifference) * size
            if (adjustedWidth < minSize) return@search null

            searchIndex
        } ?: return false

        weightMap[index] = weight
        weightMap[adjustedIndex] = weightMap.getValue(adjustedIndex) + weightDifference
        return true
    }

    /**
     * Returns if the child at this index is currently visible.
     * @param index The index whose status is being reported.
     */
    fun isVisibleAt(index: Int) = index < visibleCount

    /**
     * Attempts to resize the child at the specified index by the specified delta and returns the
     * status of the attempt.
     *
     * @param index The index to drag.
     * @param delta The amount to resize [index] by.
     */
    fun dragBy(index: Int, delta: Dp): Boolean {
        val oldWeight = weightAt(index)
        val currentSize = oldWeight * size
        val newSize = currentSize + delta
        val newWeight = (newSize / size) * weightSum
        return setWeightAt(
            index = index,
            weight = newWeight
        )
    }

    private fun checkVisibleCount() {
        check(visibleCount <= maxCount) {
            "initialVisibleCount must be less than or equal to maxCount."
        }
    }

    internal companion object SplitLayoutInstance {

        @Composable
        fun SplitLayoutState.Separators(
            separator: @Composable (paneIndex: Int, offset: Dp) -> Unit
        ) {
            if (visibleCount > 1)
                for (index in 0..<visibleCount)
                    if (index != visibleCount - 1)
                        separator(index, offsetLookup[index])
        }

        fun SplitLayoutState.updateSize(size: IntSize, density: Density) {
            this.size = with(density) { orientation.valueOf(size.toSize().toDpSize()) }
        }
    }
}

/**
 * A layout for consecutively placing resizable children along the axis of
 * [SplitLayoutState.orientation]. The children should be the same size perpendicular to
 * [SplitLayoutState.orientation].
 *
 * Children may be hidden by writing to [SplitLayoutState.visibleCount].
 *
 * @param state The state of the layout.
 * @param modifier The modifier to be applied to the layout.
 * @param itemSeparators Separators to be drawn when more than one child is visible.
 * @param itemContent the content to be drawn in each visible index.
 */
@Composable
fun SplitLayout(
    state: SplitLayoutState,
    modifier: Modifier = Modifier,
    itemSeparators: @Composable (paneIndex: Int, offset: Dp) -> Unit = { _, _ -> },
    itemContent: @Composable (Int) -> Unit,
) = with(SplitLayoutState) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .onSizeChanged {
                state.updateSize(it, density)
            },
    ) {
        when (state.orientation) {
            Orientation.Vertical -> Column(
                modifier = Modifier
                    .matchParentSize(),
            ) {
                for (index in 0..<state.maxCount) {
                    if (state.isVisibleAt(index)) Box(
                        modifier = Modifier
                            .weight(state.weightAt(index))
                    ) {
                        itemContent(index)
                    }
                    else Spacer(Modifier.size(0.dp))
                }
            }

            Orientation.Horizontal -> Row(
                modifier = Modifier
                    .matchParentSize(),
            ) {
                for (index in 0..<state.maxCount) {
                    if (state.isVisibleAt(index)) Box(
                        modifier = Modifier
                            .weight(state.weightAt(index))
                    ) {
                        itemContent(index)
                    }
                    else Spacer(Modifier.size(0.dp))
                }
            }
        }
        state.Separators(itemSeparators)
    }
}

