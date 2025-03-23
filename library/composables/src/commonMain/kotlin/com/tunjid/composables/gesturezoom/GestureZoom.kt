package com.tunjid.composables.gesturezoom

import androidx.compose.animation.core.animate
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.roundToIntSize
import com.tunjid.composables.gesturezoom.GestureZoomState.Companion.gestureZoomable
import com.tunjid.composables.gesturezoom.GestureZoomState.Options
import kotlin.math.roundToInt

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
 * @param enabled whether or not the gesture is enabled.
 * @param options configuration options for pinch to zoom behavior.
 */
@Composable
fun rememberGestureZoomState(
    zoomScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    maxScale: Float = DEFAULT_MAX_ZOOM_IN_SCALE,
    minScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    anchorZoomScale: Float = zoomScale,
    enabled: Boolean = true,
    options: Options = DefaultOptions,
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
            enabled = enabled,
            options = options,
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
 * @param enabled whether or not the gesture is enabled.
 * @param options configuration options for pinch to zoom behavior.
 */
@Stable
class GestureZoomState(
    zoomScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    enabled: Boolean = true,
    private val maxScale: Float = DEFAULT_MAX_ZOOM_IN_SCALE,
    private val minScale: Float = DEFAULT_ZOOM_OUT_SCALE,
    private val anchorZoomScale: Float = zoomScale,
    private val options: Options = DefaultOptions,
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

    var enabled by mutableStateOf(enabled)

    // Mutable state variables to hold scale and offset values
    var zoomScale by mutableFloatStateOf(zoomScale)
        private set

    var size by mutableStateOf(IntSize.Zero)
        private set

    val panOffset
        get() = Offset(
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

    /**
     * Toggles the zoom between its current scale and its [anchorZoomScale] scale or a
     * provided scale with an animation. It also resets [panOffset].
     *
     * @param newScale the new scale desired, otherwise it resets the zoom to [anchorZoomScale]
     * if its not at the anchor, or 2x zoom if it is at the anchor.
     */
    suspend fun toggleZoom(
        newScale: Float = Float.NaN,
    ) = transformMutatorMutex.mutate {
        val startingScale = zoomScale
        val finalScale = when {
            !newScale.isNaN() && newScale != startingScale -> newScale
            zoomScale != anchorZoomScale -> anchorZoomScale
            else -> DEFAULT_ZOOM_IN_SCALE
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

    data class Options(
        val scale: Scale,
        val offset: Offset,
    ) {
        /**
         * Options for how scale transforms are performed.
         */
        sealed class Scale {
            /**
             * Scale using the graphics layer without affecting the item layout size.
             */
            data object GraphicsLayer : Scale()

            /**
             * Scale by changing the layout size of the composable.
             */
            data object Layout : Scale()

            /**
             * Do not alter visual scale of the composable with the gesture.
             */
            data object None : Scale()
        }

        /**
         * Options for how position transforms are performed.
         */
        sealed class Offset {
            /**
             * Position using the graphics layer without affecting the true item layout position.
             */
            data object GraphicsLayer : Offset()

            /**
             * Change the layout position of the composable with the gesture.
             */
            data object Layout : Offset()

            /**
             * Do not alter the offset of the composable with the gesture.
             */
            data object None : Offset()
        }
    }

    companion object {
        /**
         * A [Modifier] for capturing pinch to zoom gestures. The modifier should be placed after
         * any size modifiers in the modifier chain, as the gesture is dependent on the size of the
         * layout node it is attached to.
         *
         * @param state state containing metadata about the gesture.
         */
        fun Modifier.gestureZoomable(state: GestureZoomState): Modifier =
            this
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        if (state.options.scale is Options.Scale.Layout) {
                            Constraints(
                                minWidth = (constraints.minWidth * state.zoomScale).roundToInt(),
                                maxWidth = (constraints.maxWidth * state.zoomScale).roundToInt(),
                                minHeight = (constraints.minHeight * state.zoomScale).roundToInt(),
                                maxHeight = (constraints.maxHeight * state.zoomScale).roundToInt(),
                            )
                        } else {
                            constraints
                        },
                    )
                    state.size = if (state.options.scale is Options.Scale.Layout) {
                        Size(
                            width = placeable.width / state.zoomScale,
                            height = placeable.height / state.zoomScale,
                        ).roundToIntSize()
                    } else {
                        IntSize(placeable.width, placeable.height)
                    }

                    val (x, y) = if (state.options.offset is Options.Offset.Layout) {
                        state.panOffset.round()
                    } else {
                        IntOffset.Zero
                    }

                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(x = x, y = y)
                    }
                }.transformable(
                    canPan = { state.zoomScale != DEFAULT_ZOOM_OUT_SCALE },
                    state = state.transformableState,
                    enabled = state.enabled,
                ).graphicsLayer {
                    if (state.options.scale is Options.Scale.GraphicsLayer) {
                        scaleX = state.zoomScale
                        scaleY = state.zoomScale
                    }
                    if (state.options.offset is Options.Offset.GraphicsLayer) {
                        translationX = state.offsetX
                        translationY = state.offsetY
                    }
                }

        /**
         * The default [Saver] implementation for [GestureZoomState].
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
                    if (gestureZoomState.enabled) 1f else 0f,
                    when (gestureZoomState.options.scale) {
                        Options.Scale.GraphicsLayer -> 2f
                        Options.Scale.Layout -> 1f
                        Options.Scale.None -> 0f
                    },
                    when (gestureZoomState.options.offset) {
                        Options.Offset.GraphicsLayer -> 2f
                        Options.Offset.Layout -> 1f
                        Options.Offset.None -> 0f
                    },
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
                    enabled = values[6] == 1f,
                    options = Options(
                        scale = when (values[7]) {
                            2f -> Options.Scale.GraphicsLayer
                            1f -> Options.Scale.Layout
                            else -> Options.Scale.None
                        },
                        offset = when (values[8]) {
                            2f -> Options.Offset.GraphicsLayer
                            1f -> Options.Offset.Layout
                            else -> Options.Offset.None
                        },
                    )
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

private val DefaultOptions = Options(
    scale = Options.Scale.GraphicsLayer,
    offset = Options.Offset.GraphicsLayer,
)
