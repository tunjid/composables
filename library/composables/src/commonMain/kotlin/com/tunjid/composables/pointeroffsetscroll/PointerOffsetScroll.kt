package com.tunjid.composables.pointeroffsetscroll

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import com.tunjid.composables.scrollbars.valueOf
import kotlinx.coroutines.delay

/**
 * State for utilizing [Modifier.pointerOffsetScroll].
 *
 * @param enabled The initial enabled state of the [PointerOffsetScrollState].
 *
 * @param scrollThresholdFraction The fraction that when crossed by
 * [PointerOffsetScrollState.currentOffset], will trigger a scroll. It is relative to the center of
 * the bound of the Composable [Modifier.pointerOffsetScroll] is attached to.
 *
 * @param scrollableState The scrollable state that will be scrolled when the offset threshold
 * is crossed.
 *
 * @param orientation the [Orientation] used to determine the scroll speed when scrolling the
 * [ScrollableState].
 */
@Stable
class PointerOffsetScrollState(
    enabled: Boolean = true,
    scrollThresholdFraction: Float = 0.5f,
    internal val scrollableState: ScrollableState,
    internal val orientation: Orientation,
) {
    /**
     * The current pointer [Offset] in the scrollable container. The closer it is to the extremes,
     * the faster the scroll.
     */
    var currentOffset by mutableStateOf(Offset.Unspecified)

    /**
     * Whether or not the [Modifier] is enabled.
     */
    var enabled by mutableStateOf(enabled)

    /**
     * The fraction over which when crossed, scrolling will begin. The position is relative to
     * the center of the bounding container.
     */
    var scrollThresholdFraction by mutableFloatStateOf(scrollThresholdFraction)

    internal var bottomEnd by mutableStateOf(Offset.Zero)
    internal var scrollAmount by mutableFloatStateOf(0f)
}

/**
 * A Modifier that scrolls a [ScrollableState] forwards or backwards when
 * the [PointerOffsetScrollState.currentOffset] crosses the center of the Composable [this]
 * [Modifier] is attached to by the threshold defined by
 * [PointerOffsetScrollState.scrollThresholdFraction].
 *
 * @param state the state managing the [Modifier].
 */
@Composable
fun Modifier.pointerOffsetScroll(
    state: PointerOffsetScrollState,
): Modifier {
    LaunchedEffect(state.currentOffset, state.bottomEnd, state.scrollThresholdFraction) {
        if (state.currentOffset == Offset.Unspecified) return@LaunchedEffect

        val currentValue = state.orientation.valueOf(state.currentOffset)
        val maxValue = state.orientation.valueOf(state.bottomEnd)

        val lowerThreshold = (maxValue - (maxValue * state.scrollThresholdFraction)) / 2
        val upperThreshold = maxValue - lowerThreshold

        state.scrollAmount = when (currentValue) {
            in Float.MIN_VALUE..lowerThreshold -> currentValue - lowerThreshold
            in upperThreshold..Float.MAX_VALUE -> currentValue - upperThreshold
            else -> 0f
        }
    }
    LaunchedEffect(state.scrollAmount, state.enabled) {
        if (state.scrollAmount == 0f || !state.enabled) return@LaunchedEffect

        while (true) {
            state.scrollableState.scrollBy(state.scrollAmount)
            delay(POINTER_SCROLL_DELAY_MS)
        }
    }

    return this then Modifier.onPlaced { coordinates ->
        state.bottomEnd = coordinates.boundsInRoot().let {
            Offset(it.width, it.height)
        }
    }
}

private const val POINTER_SCROLL_DELAY_MS = 10L