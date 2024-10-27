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
import com.tunjid.composables.valueOf
import kotlinx.coroutines.delay

/**
 * State for utilizing [Modifier.pointerOffsetScroll].
 *
 *
 * @param scrollableState the scrollable state that will be scrolled when the offset threshold
 * is crossed.
 *
 * @param orientation the [Orientation] used to determine the scroll speed when scrolling the
 * [ScrollableState].
 * @param enabled the initial enabled state of the [PointerOffsetScrollState].
 *
 * @param scrollThresholdFraction the fraction of the length of the side of the
 * container by its [orientation], that when crossed by [PointerOffsetScrollState.currentOffset],
 * will trigger a scroll. It is measured  relative to the center of the bounds of the Composable
 * [Modifier.pointerOffsetScroll] is attached to.
 *
 * @param scrollAmountMultiplier a multiplier for how quickly the pointer offset should scroll.
 */
@Stable
class PointerOffsetScrollState(
    internal val scrollableState: ScrollableState,
    internal val orientation: Orientation,
    enabled: Boolean = true,
    scrollThresholdFraction: Float = 0.5f,
    scrollAmountMultiplier: Float = 1f,
) {
    /**
     * The current pointer [Offset] in the scrollable container. The closer it is to the extremes,
     * the faster the scroll.
     */
    var currentOffset by mutableStateOf(Offset.Zero)

    /**
     * Whether or not the [Modifier] is enabled.
     */
    var enabled by mutableStateOf(enabled)

    /**
     * The fraction of the length of the side of the container by its [orientation],
     * that when crossed by [PointerOffsetScrollState.currentOffset], scrolling will begin.
     * It is measured  relative to the center of the bounds of the Composable
     * [Modifier.pointerOffsetScroll] is attached to
     */
    var scrollThresholdFraction by mutableFloatStateOf(scrollThresholdFraction)

    /**
     * A multiplier for how quickly the pointer offset should scroll the [scrollableState].
     */
    var scrollAmountMultiplier by mutableFloatStateOf(scrollAmountMultiplier)

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
 *
 * @sample com.tunjid.demo.common.app.demos.PointerOffsetLazyListDemoScreen
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
    LaunchedEffect(state.scrollAmount, state.enabled, state.scrollAmountMultiplier) {
        if (state.scrollAmount == 0f || !state.enabled) return@LaunchedEffect

        while (true) {
            state.scrollableState.scrollBy(
                value = state.scrollAmount * state.scrollAmountMultiplier
            )
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