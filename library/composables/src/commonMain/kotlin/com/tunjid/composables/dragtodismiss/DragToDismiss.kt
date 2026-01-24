package com.tunjid.composables.dragtodismiss

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * State for utilizing [Modifier.dragToDismiss].
 *
 * @param enabled The initial enabled state of the [DragToDismissState].
 * @param animationSpec The animation spec used to reset the dragged item back
 * to its starting [Offset].
 */
@Stable
class DragToDismissState(
    internal val coroutineScope: CoroutineScope,
    enabled: Boolean = true,
    animationSpec: AnimationSpec<Offset> = DefaultDragToDismissSpring,
) {
    /**
     * Whether or not drag to dismiss is available.
     */
    var enabled by mutableStateOf(enabled)

    /**
     * The animation spec used to reset the dragged item back
     * to its starting [Offset].
     */
    var animationSpec by mutableStateOf(animationSpec)

    /**
     * The current [Offset] from the starting position of the drag.
     */
    var offset by mutableStateOf(Offset.Zero)
        internal set

    internal val draggable2DState = Draggable2DState { dragAmount ->
        offset += dragAmount
    }

    internal var startDragImmediately by mutableStateOf(false)
}

/**
 * Remembers a [DragToDismissState] for utilizing [Modifier.dragToDismiss].
 *
 * @param enabled The initial enabled state of the [DragToDismissState].
 * @param animationSpec The animation spec used to reset the dragged item back
 * to its starting [Offset].
 */
@Composable
fun rememberUpdatedDragToDismissState(
    enabled: Boolean = true,
    animationSpec: AnimationSpec<Offset> = DefaultDragToDismissSpring,
): DragToDismissState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        DragToDismissState(
            coroutineScope = coroutineScope,
            enabled = enabled,
            animationSpec = animationSpec,
        )
    }.also {
        it.enabled = enabled
        it.animationSpec = animationSpec
    }
}

/**
 * A Modifier for performing the drag to dismiss UI gesture pattern. When the dragged item
 * is not being dismissed and is being reset into its original position, the reset may be
 * interrupted by dragging it again.
 *
 * @param state state controlling the properties of the [Modifier].
 * @param shouldDismiss a lambda for checking if the threshold for the drag offset for
 * dismissal has been reached.
 * It provides two arguments, the current displacement [Offset], and the [Velocity] at which
 * the drag was stopped. Return true if the offset has been reached and the composable should be
 * dismissed, else false for the composable to be animated back to its starting position.
 * @param onStart called when the drag commences and the composable has been displaced
 * from its original position.
 * @param onCancelled called when the drag to dismiss gesture has been cancelled because the drag
 * gesture stopped and the [shouldDismiss] returned false. It may be invoked up to twice
 * per session, with each invocation guaranteed to have different arguments:
 * - First: Invoked with false, signifying the cancellation of the gesture, but that the
 * Composable is not yet back at its starting position.
 * - Second: Invoked with true, signifying the Composable has settled back into its original
 * position.
 * It will only be called if the reset animation completes without being cancelled.
 * @param onDismissed called when the composable has been dragged past its dismissal
 * threshold and should be dismissed. Note that the Composable will have its displacement
 * [Offset] reset to [Offset.Zero] immediately after this is called.
 *
 * @sample com.tunjid.demo.common.app.demos.DragToDismissContainer
 */
fun Modifier.dragToDismiss(
    state: DragToDismissState,
    shouldDismiss: (Offset, Velocity) -> Boolean,
    onStart: () -> Unit = {},
    onCancelled: (reset: Boolean) -> Unit = {},
    onDismissed: () -> Unit,
): Modifier = draggable2D(
    state = state.draggable2DState,
    startDragImmediately = state.startDragImmediately,
    enabled = state.enabled,
    onDragStarted = {
        onStart()
    },
    onDragStopped = { velocity ->
        if (shouldDismiss(state.offset, velocity)) {
            state.startDragImmediately = false
            onDismissed()
            // Reset offset back to zero.
            state.offset = Offset.Zero
        } else {
            onCancelled(false)
            state.coroutineScope.launch {
                try {
                    state.startDragImmediately = true
                    state.draggable2DState.drag {
                        animate(
                            typeConverter = Offset.VectorConverter,
                            initialValue = state.offset,
                            targetValue = Offset.Zero,
                            initialVelocity = Offset(
                                x = velocity.x,
                                y = velocity.y,
                            ),
                            animationSpec = state.animationSpec,
                            block = { value, _ ->
                                dragBy(value - state.offset)
                            },
                        )
                    }
                    // Notify that it has been reset.
                    onCancelled(true)
                } finally {
                    state.startDragImmediately = false
                    // Reset offset if canceled and modifier is out of the composition, otherwise
                    // allow user catch the drag as it settles.
                    if (!state.coroutineScope.isActive) state.offset = Offset.Zero
                }
            }
        }
    },
)

private val DefaultDragToDismissSpring = spring<Offset>()
