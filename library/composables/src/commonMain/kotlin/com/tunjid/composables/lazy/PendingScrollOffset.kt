package com.tunjid.composables.lazy

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch

/**
 * Defers scrolling to the [MutableFloatState.value] in the [MutableFloatState] returned
 * until the next time this effect enters the composition instead of executing it immediately.
 *
 * For example, consider an item that is scrolled to, and interacting with it causes the
 * item composed to leave the composition. This [MutableFloatState] can be written to with
 * the item's current position, such that when returned to and the scrollable container is
 * recomposed, the item interacted with can be recomposed with it firmly in focus
 * as defined by the value written into this [MutableFloatState].
 */
@Composable
fun ScrollableState.pendingScrollOffsetState(): MutableFloatState {
    val pendingScrollState = rememberSaveable { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        if (pendingScrollState.floatValue == 0f) return@DisposableEffect onDispose { }
        val job = scope.launch {
            scrollBy(pendingScrollState.floatValue)
            pendingScrollState.floatValue = 0f
        }
        onDispose {
            job.cancel()
        }
    }

    return pendingScrollState
}