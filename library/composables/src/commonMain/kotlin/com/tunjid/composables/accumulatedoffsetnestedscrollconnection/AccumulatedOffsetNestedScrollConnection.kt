package com.tunjid.composables.accumulatedoffsetnestedscrollconnection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.math.max
import kotlin.math.min

/**
 * A [NestedScrollConnection] that accumulates offsets during a nested scroll without
 * consuming the nested scroll deltas.
 *
 * It allows for reacting precisely to scroll events.
 *
 * @param maxOffset the maximum offset that can be accumulated as a result of scroll events.
 * @param minOffset the minimum offset that can be accumulated as a result of scroll events.
 * @param invert allows for inverting positive scroll deltas as negative and vice versa.
 * @param initialOffset the initial [Offset] of the [AccumulatedOffsetNestedScrollConnection].
 */
@Stable
class AccumulatedOffsetNestedScrollConnection(
    private val maxOffset: Offset,
    private val minOffset: Offset,
    private val invert: Boolean = false,
    initialOffset: Offset = Offset.Zero,
) : NestedScrollConnection {

    var offset by mutableStateOf(initialOffset)
        private set

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val adjusted =
            if (invert) offset - available
            else offset + available

        offset = adjusted.copy(
            x = max(
                min(adjusted.x, maxOffset.x),
                minOffset.x,
            ),
            y = max(
                min(adjusted.y, maxOffset.y),
                minOffset.y,
            ),
        )

        return Offset.Zero
    }
}

/**
 * Remembers [NestedScrollConnection] that accumulates offsets during a nested scroll without
 * consuming the nested scroll deltas.
 *
 * It allows for reacting precisely to scroll events.
 *
 * @param maxOffset the maximum offset that can be accumulated as a result of scroll events.
 * @param minOffset the minimum offset that can be accumulated as a result of scroll events.
 * @param invert allows for inverting positive scroll deltas as negative and vice versa.
 * @param initialOffset the initial [Offset] of the [AccumulatedOffsetNestedScrollConnection].
 */
@Composable
fun rememberAccumulatedOffsetNestedScrollConnection(
    maxOffset: Density.() -> Offset,
    minOffset: Density.() -> Offset,
    invert: Boolean = false,
    initialOffset: Offset = Offset.Zero,
): AccumulatedOffsetNestedScrollConnection {
    var savedOffset by rememberSaveable {
        mutableLongStateOf(
            packFloats(
                val1 = initialOffset.x,
                val2 = initialOffset.y,
            )
        )
    }
    val density = LocalDensity.current
    val connection = remember {
        AccumulatedOffsetNestedScrollConnection(
            invert = invert,
            maxOffset = density.maxOffset(),
            minOffset = density.minOffset(),
            initialOffset = Offset(
                x = unpackFloat1(savedOffset),
                y = unpackFloat2(savedOffset),
            )
        )
    }
    SideEffect {
        savedOffset = packFloats(
            val1 = connection.offset.x,
            val2 = connection.offset.y,
        )
    }
    return connection
}