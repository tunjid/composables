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

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.animateToWithDecay
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.jvm.JvmInline
import kotlin.math.max
import kotlin.math.roundToInt

enum class CollapsingHeaderStatus {
    Collapsed, Expanded
}

/**
 * remembers the state for managing the [CollapsingHeaderLayout] composable.
 * @param collapsedHeight: the height of the header when collapsed.
 * @param initialExpandedHeight: the initial expanded height of the expanded header before it is
 * measured. This should be an estimate, the expanded height is determined by the size of
 * headerContent in [CollapsingHeaderLayout].
 * @param snapThreshold the threshold at which the header will snap to its closest anchor state, if
 * neither fully expanded or collapsed.
 * @param initialStatus set if the header is initially expanded or collapsed.
 * @param flingBehavior Optionally configure how the header performs flings.
 * See [Modifier.anchoredDraggable]
 */
@Composable
fun rememberCollapsingHeaderState(
    collapsedHeight: Float,
    initialExpandedHeight: Float,
    snapThreshold: Float = 0.25f,
    initialStatus: CollapsingHeaderStatus = CollapsingHeaderStatus.Expanded,
    flingBehavior: FlingBehavior? = null,
): CollapsingHeaderState = rememberSaveable(
    saver = CollapsingHeaderState.Saver(
        collapsedHeight = collapsedHeight,
        flingBehavior = flingBehavior,
    ),
    init = {
        CollapsingHeaderState(
            collapsedHeight = collapsedHeight,
            initialExpandedHeight = initialExpandedHeight,
            snapThreshold = snapThreshold,
            initialStatus = initialStatus,
        )
    }
)

/**
 * remembers the state for managing the [CollapsingHeaderLayout] composable.
 * @param collapsedHeight: the height of the header when collapsed.
 * @param initialExpandedHeight: the initial expanded height of the expanded header before it is
 * measured. This should be an estimate, the expanded height is determined by the size of
 * headerContent in [CollapsingHeaderLayout].
 * @param snapThreshold the threshold at which the header will snap to its closest anchor state, if
 * neither fully expanded or collapsed.
 * @param initialStatus set if the header is initially expanded or collapsed.
 * @param flingBehavior Optionally configure how the header performs flings.
 * See [Modifier.anchoredDraggable]
 */
@Suppress("unused")
@Composable
fun rememberCollapsingHeaderState(
    collapsedHeight: Density.() -> Float,
    initialExpandedHeight: Density.() -> Float,
    snapThreshold: Float = 0.25f,
    initialStatus: CollapsingHeaderStatus = CollapsingHeaderStatus.Expanded,
    flingBehavior: FlingBehavior? = null,
): CollapsingHeaderState {
    val density = LocalDensity.current
    return rememberCollapsingHeaderState(
        collapsedHeight = density.collapsedHeight(),
        initialExpandedHeight = density.initialExpandedHeight(),
        snapThreshold = snapThreshold,
        initialStatus = initialStatus,
        flingBehavior = flingBehavior,
    )
}

/**
 * State for managing the [CollapsingHeaderLayout] composable.
 * @param collapsedHeight the height of the header when collapsed.
 * @param initialExpandedHeight the initial expanded height of the expanded header before it is
 * measured. This should be an estimate, the expanded height is determined by the size of
 * headerContent in [CollapsingHeaderLayout].
 * @param snapThreshold the threshold at which the header will snap to its closest anchor state, if
 * neither fully expanded or collapsed.
 * @param initialStatus set if the header is initially expanded or collapsed.
 * @param flingBehavior Optionally configure how the header performs flings.
 * See [Modifier.anchoredDraggable]
 */
@Stable
class CollapsingHeaderState(
    collapsedHeight: Float,
    initialExpandedHeight: Float,
    internal val snapThreshold: Float,
    initialStatus: CollapsingHeaderStatus = CollapsingHeaderStatus.Expanded,
    internal val flingBehavior: FlingBehavior? = null,
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
        initialValue = initialStatus,
        anchors = currentDraggableAnchors(),
    )

    /**
     * Animate to the specified [status].
     * @param status the status to animate to.
     */
    suspend fun animateTo(status: CollapsingHeaderStatus) {
        anchoredDraggableState.animateTo(status)
    }

    /**
     * Snap to the specified [status].
     * @param status the status to snap to.
     */
    suspend fun snapTo(status: CollapsingHeaderStatus) {
        anchoredDraggableState.snapTo(status)
    }

    private fun updateAnchors() = anchoredDraggableState.updateAnchors(
        currentDraggableAnchors()
    )

    private fun currentDraggableAnchors() = DraggableAnchors {
        CollapsingHeaderStatus.Collapsed at collapsedHeight
        CollapsingHeaderStatus.Expanded at expandedHeight
    }

    companion object {
        /**
         * The default [Saver] implementation for [CollapsingHeaderState].
         */
        fun Saver(
            collapsedHeight: Float,
            flingBehavior: FlingBehavior?,
        ) = listSaver<CollapsingHeaderState, Float>(
            save = { headerState ->
                listOf(
                    headerState.expandedHeight,
                    headerState.progress,
                    headerState.snapThreshold,
                )
            },
            restore = { (expandedHeight, progress, snapThreshold) ->
                CollapsingHeaderState(
                    collapsedHeight = collapsedHeight,
                    initialExpandedHeight = expandedHeight,
                    snapThreshold = snapThreshold,
                    initialStatus =
                        if (progress > 0.5f) CollapsingHeaderStatus.Collapsed
                        else CollapsingHeaderStatus.Expanded,
                    flingBehavior = flingBehavior,
                )
            }
        )
    }
}

/**
 * A layout that allows for the collapsing header pattern with the [headerContent] Composable
 * by placing the [body] Composable underneath it.
 *
 * - Scroll events on the [headerContent] are scroll to the [body] and update the [state].
 * - Scroll events DO NOT automatically scroll the header. This is to allow functionality like
 * pinning. To scroll the header, apply the offset from [CollapsingHeaderState.translation] to
 * it using [Modifier.offset] as seen in the sample below.
 *
 * @param state the backing [CollapsingHeaderState]
 * @param modifier the modifier for the layout.
 * @param headerContent the composable to render in the header.
 * @param body the composable to render in the body. It should support nested scrolling.
 * @sample com.tunjid.demo.common.app.demos.utilities.DemoCollapsingHeader
 */
@Composable
fun CollapsingHeaderLayout(
    state: CollapsingHeaderState,
    modifier: Modifier = Modifier,
    headerContent: @Composable () -> Unit,
    body: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Vertical,
                flingBehavior = state.flingBehavior,
            )
            .nestedScroll(
                connection = state.nestedScrollConnection(),
            ),
        content = {
            Box(
                modifier = Modifier
                    .onSizeChanged { state.expandedHeight = it.height.toFloat() },
            ) {
                headerContent()
            }
            Box(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(
                            width = placeable.width,
                            height = max(
                                a = 0,
                                b = placeable.height - state.collapsedHeight.roundToInt(),
                            ),
                        ) {
                            placeable.place(
                                x = 0,
                                y = state.anchoredDraggableState.offset.roundToInt()
                            )
                        }
                    }
            ) {
                body()
            }
        }
    )
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

private fun CollapsingHeaderState.nestedScrollConnection() =
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset = when (val delta = available.y) {
            in -Float.MAX_VALUE..-Float.MIN_VALUE -> anchoredDraggableState.dispatchRawDelta(delta)
                .toOffset()

            else -> Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = anchoredDraggableState.dispatchRawDelta(delta = available.y).toOffset()

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity
        ): Velocity {
            val currentValue = anchoredDraggableState.currentValue

            if (available.y > 0 && currentValue == CollapsingHeaderStatus.Collapsed) {
                return animateToStatusWithVelocity(
                    available = available,
                    status = CollapsingHeaderStatus.Expanded
                )
            }
            if (available.y < 0 && currentValue == CollapsingHeaderStatus.Expanded) {
                return animateToStatusWithVelocity(
                    available = available,
                    status = CollapsingHeaderStatus.Collapsed
                )
            }

            val hasNoInertia = available == Velocity.Zero && consumed == Velocity.Zero

            if (hasNoInertia && progress < snapThreshold) {
                return animateToStatusWithVelocity(
                    available = available,
                    status = CollapsingHeaderStatus.Expanded
                )
            }
            if (hasNoInertia && progress > 1 - snapThreshold) {
                return animateToStatusWithVelocity(
                    available = available,
                    status = CollapsingHeaderStatus.Collapsed,
                )
            }

            return super.onPostFling(consumed, available)
        }

        private suspend fun animateToStatusWithVelocity(
            available: Velocity,
            status: CollapsingHeaderStatus,
        ) = Velocity(
            x = 0f,
            y = anchoredDraggableState.animateToWithDecay(
                targetValue = status,
                velocity = available.y,
            )
        )
    }

private fun Float.toOffset() = Offset(0f, this)
