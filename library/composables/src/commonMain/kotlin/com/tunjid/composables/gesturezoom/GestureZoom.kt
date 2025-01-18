package com.tunjid.composables.gesturezoom

import androidx.compose.animation.core.animate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.tunjid.composables.accumulatedoffsetnestedscrollconnection.AccumulatedOffsetNestedScrollConnection
import com.tunjid.composables.gesturezoom.GestureZoomState.Companion.gestureZoomable

/**
 * Remembers a [GestureZoomState] for driving gesture based zoom with
 * [GestureZoomState.gestureZoomable].
 *
 * @param zoomScale the starting zoom scale of the [GestureZoomState].
 * @param offsetX the starting pan offset of the state on the x axis.
 * @param offsetY the starting pan offset of the state on the y axis.
 * @param maxScale the maximum zoom scale allowed. There's a hard 10x limit.
 * @param minScale the minimum zoom scale allowed.
 * @param anchorZoomScale the zoom scale that when passed, enables pan gestures. This allows
 * for pan gestures to be seen by other gesture handlers at [anchorZoomScale].
 */
@Composable
fun rememberGestureZoomState(
    zoomScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    maxScale: Float = DEFAULT_MAX_ZOOM_IN_SCALE,
    minScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    anchorZoomScale: Float = zoomScale,
): GestureZoomState = rememberSaveable(
    saver = GestureZoomState.Saver,
    init = {
        GestureZoomState(
            maxScale = maxScale,
            minScale = minScale,
            zoomScale = zoomScale,
            offsetX = offsetX,
            offsetY = offsetY,
            anchorZoomScale = anchorZoomScale,
        )
    }
)

/**
 * State for managing a inch to zoom gesture. The zoomed in scale can be read from
 * [GestureZoomState.zoomScale].
 *
 * @param zoomScale the starting zoom scale of the [GestureZoomState].
 * @param offsetX the starting pan offset of the state on the x axis.
 * @param offsetY the starting pan offset of the state on the y axis.
 * @param maxScale the maximum zoom scale allowed. There's a hard 10x limit.
 * @param minScale the minimum zoom scale allowed.
 * @param anchorZoomScale the zoom scale that when passed, enables pan gestures. This allows
 * for pan gestures to be seen by other gesture handlers at [anchorZoomScale].
 */
@Stable
class GestureZoomState(
    zoomScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    private val maxScale: Float = DEFAULT_MAX_ZOOM_IN_SCALE,
    private val minScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    private val anchorZoomScale: Float = zoomScale,
) {
    init {
        require(maxScale > DEFAULT_ZOOM_OUT_SCALE && maxScale < ABSOLUTE_MAX_ZOOM_IN_SCALE) {
            "maxScale must be between $DEFAULT_ZOOM_OUT_SCALE and $ABSOLUTE_MAX_ZOOM_IN_SCALE"
        }
        require(minScale < maxScale) {
            "minScale must be less than max scale"
        }
    }

    // Ensure only one driver of gesture values
    private val transformMutatorMutex = MutatorMutex()

    // Mutable state variables to hold scale and offset values
    var zoomScale by mutableFloatStateOf(zoomScale)
        private set

    var size by mutableStateOf(IntSize.Zero)
        private set

    val panOffset get() = Offset(
        x = offsetX,
        y = offsetY
    )

    // These offsets are separated instead of using a singular Offset() to prevent allocations
    // when using mutableStateOf(Offset())
    private var offsetX by mutableFloatStateOf(offsetX)
    private var offsetY by mutableFloatStateOf(offsetY)

    // Remember the initial offset
    private var initialOffset by mutableStateOf(Offset(0f, 0f))

    private val transformableState = TransformableState { zoom, pan, _ ->
        transformMutatorMutex.tryMutate {
            onEvent(
                pan = pan,
                zoom = zoom,
            )
        }
    }

    /**
     * Try to update [zoomScale] to [updatedScale] if nothing else is mutating the
     * [GestureZoomState].
     *
     * @return true if [zoomScale] was successfully updated, false otherwise.
     */
    fun updateZoomScale(
        updatedScale: Float,
    ) = transformMutatorMutex.tryMutate {
        require(updatedScale in minScale..maxScale) {
            "Updated scale is not in the min and max scale range"
        }
        this.zoomScale = updatedScale
    }

    /**
     * Try to update the pan of state to [updatedPan] if nothing else is mutating the
     * [GestureZoomState].
     *
     * @return true if the pan was successfully updated, false otherwise.
     */
    fun updatePan(
        updatedPan: Offset,
    ) = transformMutatorMutex.tryMutate {
        coercePanOffset(updatedPan)
    }

    suspend fun toggleZoom() = transformMutatorMutex.mutate {
        val startingScale = zoomScale
        val finalScale =
            if (zoomScale != anchorZoomScale) {
                anchorZoomScale
            } else {
                DEFAULT_ZOOM_IN_SCALE
            }

        val startingOffset = Offset(offsetX, offsetY)

        val scaleDifference = finalScale - startingScale
        val offsetDifference = initialOffset - startingOffset

        animate(
            initialValue = 0f,
            targetValue = 1f,
            block = { value, _ ->
                zoomScale = startingScale + (value * scaleDifference)
                offsetX = startingOffset.x + (value * offsetDifference.x)
                offsetY = startingOffset.y + (value * offsetDifference.y)
            },
        )
        zoomScale = finalScale
        offsetX = initialOffset.x
        offsetY = initialOffset.y
    }

    private fun onEvent(pan: Offset, zoom: Float) {
        // Update scale with the zoom
        val newScale = zoomScale * zoom
        zoomScale = newScale.coerceIn(minScale, maxScale)

        // Calculate new offsets based on zoom and pan
        val centerX = size.width / 2
        val centerY = size.height / 2
        val offsetXChange = (centerX - offsetX) * (newScale / zoomScale - 1)
        val offsetYChange = (centerY - offsetY) * (newScale / zoomScale - 1)

        // Update offsets while ensuring they stay within bounds
        if (zoomScale * zoom <= maxScale) {
            coercePanOffset(
                update = Offset(
                    x = offsetX + (pan.x * zoomScale * SLOW_MOVEMENT) + offsetXChange,
                    y = offsetY + (pan.y * zoomScale * SLOW_MOVEMENT) + offsetYChange,
                )
            )
        }
        // Store initial offset on pan
        if (pan != Offset.Zero && initialOffset == Offset.Zero) {
            initialOffset = Offset(offsetX, offsetY)
        }
    }

    private fun coercePanOffset(
        update: Offset,
    ) {
        // Calculate min and max offsets
        val maxOffsetX = (size.width / 2) * (zoomScale - 1)
        val minOffsetX = -maxOffsetX
        val maxOffsetY = (size.height / 2) * (zoomScale - 1)
        val minOffsetY = -maxOffsetY

        // Update offsets while ensuring they stay within bounds
        offsetX = update.x.coerceIn(minOffsetX, maxOffsetX)
        offsetY = update.y.coerceIn(minOffsetY, maxOffsetY)
    }

    companion object {
        /**
         * A [Modifier] for capturing pinch to zoom gestures. The modifier should be placed after
         * any size modifiers in the modifier chain, as the gesture is dependent on the size of the
         * layout node it is attached to.
         *
         * @param state state containing metadata about the gesture.
         */
        @OptIn(ExperimentalFoundationApi::class)
        fun Modifier.gestureZoomable(state: GestureZoomState): Modifier =
            this
                .onSizeChanged {
                    state.size = IntSize(it.width, it.height)
                }.transformable(
                    canPan = { state.zoomScale != state.anchorZoomScale },
                    state = state.transformableState,
                ).graphicsLayer {
                    scaleX = state.zoomScale
                    scaleY = state.zoomScale
                    translationX = state.offsetX
                    translationY = state.offsetY
                }

        /**
         * The default [Saver] implementation for [AccumulatedOffsetNestedScrollConnection].
         */
        val Saver = listSaver(
            save = { gestureZoomState ->
                listOf(
                    gestureZoomState.zoomScale,
                    gestureZoomState.offsetX,
                    gestureZoomState.offsetY,
                    gestureZoomState.maxScale,
                    gestureZoomState.minScale,
                    gestureZoomState.anchorZoomScale,
                )
            },
            restore = { values ->
                GestureZoomState(
                    zoomScale = values[0],
                    offsetX = values[1],
                    offsetY = values[2],
                    maxScale = values[3],
                    minScale = values[4],
                    anchorZoomScale = values[5],
                )
            }
        )
    }
}

private const val DEFAULT_ZOOM_OUT_SCALE = 1f
private const val DEFAULT_ZOOM_IN_SCALE = 2f
private const val DEFAULT_MAX_ZOOM_IN_SCALE = 4f
private const val ABSOLUTE_MAX_ZOOM_IN_SCALE = 10f
private const val SLOW_MOVEMENT = 0.5f
