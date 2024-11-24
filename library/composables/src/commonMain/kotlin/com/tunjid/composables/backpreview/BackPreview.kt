package com.tunjid.composables.backpreview

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import kotlin.math.roundToInt

/**
 * State for driving a material 3 back preview
 *
 * @param pointerOffset The current [IntOffset] for the gesture driving the preview.
 * @param progress The current progress of the gesture:
 * - [Float.NaN] when the gesture is not in progress
 * - Values between 0f and 1f otherwise
 * @param minScale The minimum scale the layout reaches as [progress] goes from [0f -1f].
 * @param atStart true if the gesture started at the start of the screen, false if from the end.
 */
@Stable
class BackPreviewState(
    pointerOffset: IntOffset = IntOffset.Zero,
    progress: Float = Float.NaN,
    minScale: Float = 0.9f,
    atStart: Boolean = false,
) {
    var pointerOffset by mutableStateOf(pointerOffset)
    var progress by mutableFloatStateOf(progress)
    var minScale by mutableFloatStateOf(minScale)
    var atStart by mutableStateOf(atStart)

    internal var initialPointerOffset by mutableStateOf(IntOffset.Zero)
}

/**
 * Previews back content as specified by the material motion
 * [spec]( https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back#motion-specs)
 *
 * @param state The backing state driving the preview.
 */
fun Modifier.backPreview(
    state: BackPreviewState
): Modifier = layout { measurable, constraints ->
    val touchOffset = state.pointerOffset
    val progress = state.progress
    val scale = 1f - (progress * (1 - state.minScale))

    if (progress.isNaN()) state.initialPointerOffset = IntOffset.Zero
    if (state.initialPointerOffset == IntOffset.Zero && !progress.isNaN()) state.initialPointerOffset =
        state.pointerOffset

    val placeable = measurable.measure(
        if (progress.isNaN()) constraints
        else constraints.copy(
            maxWidth = (constraints.maxWidth * scale).roundToInt(),
            minWidth = (constraints.minWidth * scale).roundToInt(),
            maxHeight = (constraints.maxHeight * scale).roundToInt(),
            minHeight = (constraints.minHeight * scale).roundToInt(),
        )
    )
    if (progress.isNaN()) return@layout layout(placeable.width, placeable.height) {
        placeable.place(0, 0)
    }

    val paneWidth = (placeable.width * scale).fastRoundToInt()
    val paneHeight = (placeable.height * scale).fastRoundToInt()

    val scaledWidth = paneWidth * scale
    val spaceOnEachSide = (paneWidth - scaledWidth) / 2
    val margin = (BACK_PREVIEW_PADDING * progress).dp.roundToPx()

    val xOffset = ((spaceOnEachSide - margin) * when {
        state.atStart -> 1
        else -> -1
    }).toInt()

    val maxYShift = ((paneHeight / 20) - BACK_PREVIEW_PADDING)
    val isOrientedHorizontally = paneWidth > paneHeight
    val screenSize = when {
        isOrientedHorizontally -> paneWidth
        else -> paneHeight
    }.dp.roundToPx()
    val initialTouchPoint = when {
        isOrientedHorizontally -> state.initialPointerOffset.x
        else -> state.initialPointerOffset.y
    }
    val touchPoint = when {
        isOrientedHorizontally -> touchOffset.x
        else -> touchOffset.y
    }
    val verticalProgress = (touchPoint - initialTouchPoint) / screenSize.toFloat()
    val yOffset = (verticalProgress * maxYShift).fastRoundToInt()

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x = xOffset, y = yOffset)
    }
}

private const val BACK_PREVIEW_PADDING = 8
