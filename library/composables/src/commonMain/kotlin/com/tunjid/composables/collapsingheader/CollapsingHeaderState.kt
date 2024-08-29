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

package com.tunjid.composables.collapsingheader

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

enum class CollapsingHeaderStatus {
    Collapsed, Expanded
}

/**
 * State for managing the [CollapsingHeader] composable.
 * @param collapsedHeight: The height of the header when collapsed.
 * @param initialExpandedHeight: The initial expanded height of the expanded header before it is
 * measured. This should be an estimate, the expanded height is determined by the size of
 * headerContent in [CollapsingHeader].
 * @param decayAnimationSpec The animation spec that will be used when flinging with a large enough
 * velocity to reach or cross between expanded and collapsed states.
 * @param snapAnimationSpec The animation spec used to animate between collapsed and expanded
 * states.
 */
@Stable
@OptIn(ExperimentalFoundationApi::class)
class CollapsingHeaderState(
    collapsedHeight: Float,
    initialExpandedHeight: Float,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    snapAnimationSpec: AnimationSpec<Float> = tween()
) {

    private var anchors by mutableLongStateOf(
        Anchors(
            collapsedHeight = collapsedHeight,
            expandedHeight = initialExpandedHeight
        ).packedValue
    )

    /**
     * The height of the header when it is fully expanded.
     */
    var expandedHeight: Float
        get() = Anchors(anchors).expandedHeight
        internal set(value) {
            anchors = Anchors(
                collapsedHeight = collapsedHeight,
                expandedHeight = value
            ).packedValue
            updateAnchors()
        }

    /**
     * The height of the header when it is fully collapsed.
     */
    var collapsedHeight: Float
        get() = Anchors(anchors).collapsedHeight
        set(value) {
            anchors = Anchors(
                collapsedHeight = value,
                expandedHeight = expandedHeight
            ).packedValue
            updateAnchors()
        }

    /**
     * The distance the header has been collapsed from its expanded height.
     */
    val translation: Float get() = expandedHeight - anchoredDraggableState.requireOffset()

    /**
     * The progress between the expanded and collapsed states.
     * It goes from 0F at expanded to 1F at collapsed.
     */
    val progress: Float get() = translation / (expandedHeight - collapsedHeight)

    internal val anchoredDraggableState = AnchoredDraggableState(
        initialValue = CollapsingHeaderStatus.Collapsed,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { 100f },
        snapAnimationSpec = snapAnimationSpec,
        decayAnimationSpec = decayAnimationSpec,
        anchors = currentDraggableAnchors()
    )

    private fun updateAnchors() = anchoredDraggableState.updateAnchors(
        currentDraggableAnchors()
    )

    private fun currentDraggableAnchors() = DraggableAnchors {
        CollapsingHeaderStatus.Collapsed at expandedHeight
        CollapsingHeaderStatus.Expanded at collapsedHeight
    }
}

/**
 * A collapsing header implementation that has anchored positions.
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CollapsingHeader(
    state: CollapsingHeaderState,
    headerContent: @Composable () -> Unit,
    body: @Composable () -> Unit,
) {
    val scrollableState = rememberScrollableState(
        consumeScrollDelta = state.anchoredDraggableState::dispatchRawDelta
    )
    Box(
        modifier = Modifier.scrollable(
            state = scrollableState,
            orientation = Orientation.Vertical,
        )
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { state.expandedHeight = it.height.toFloat() },
            content = {
                headerContent()
            }
        )
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = state.anchoredDraggableState.offset.roundToInt()
                    )
                }
                .anchoredDraggable(
                    state = state.anchoredDraggableState,
                    orientation = Orientation.Vertical
                )
                .nestedScroll(
                    connection = state.anchoredDraggableState.nestedScrollConnection(),
                ),
            content = {
                body()
            }
        )
    }
}

/**
 * Packed float class to use [mutableLongStateOf] to hold state for expanded and collapsed heights.
 */
@Immutable
@JvmInline
private value class Anchors(
    val packedValue: Long,
)

private fun Anchors(
    collapsedHeight: Float,
    expandedHeight: Float,
) = Anchors(
    packFloats(
        val1 = collapsedHeight,
        val2 = expandedHeight,
    ),
)

private val Anchors.collapsedHeight
    get() = unpackFloat1(packedValue)


private val Anchors.expandedHeight
    get() = unpackFloat2(packedValue)

@OptIn(ExperimentalFoundationApi::class)
private fun AnchoredDraggableState<CollapsingHeaderStatus>.nestedScrollConnection() =
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset = when (val delta = available.y) {
            in -Float.MAX_VALUE..-Float.MIN_VALUE -> dispatchRawDelta(delta).toOffset()
            else -> Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset = dispatchRawDelta(delta = available.y).toOffset()

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity
        ): Velocity {
            settle(velocity = available.y)
            return super.onPostFling(consumed, available)
        }
    }

private fun Float.toOffset() = Offset(0f, this)
