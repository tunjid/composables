package com.tunjid.composables.accumulatedoffsetnestedscrollconnection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
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
    val density = LocalDensity.current
    val connection = rememberSaveable(
        saver = AccumulatedOffsetNestedScrollConnection.Saver,
        init = {
            AccumulatedOffsetNestedScrollConnection(
                invert = invert,
                maxOffset = density.maxOffset(),
                minOffset = density.minOffset(),
                initialOffset = initialOffset,
            )
        }
    )
    return connection
}


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

    companion object {
        /**
         * The default [Saver] implementation for [AccumulatedOffsetNestedScrollConnection].
         */
        val Saver = listSaver(
            save = { connection ->
                listOf(
                    packFloats(
                        val1 = connection.maxOffset.x,
                        val2 = connection.maxOffset.y,
                    ),
                    packFloats(
                        val1 = connection.minOffset.x,
                        val2 = connection.minOffset.y,
                    ),
                    if (connection.invert) 1 else 0,
                )
            },
            restore = { (packedMaxOffset, packedMinOffset, invertLong) ->
                AccumulatedOffsetNestedScrollConnection(
                    maxOffset = Offset(
                        unpackFloat1(packedMaxOffset),
                        unpackFloat2(packedMaxOffset),
                    ),
                    minOffset = Offset(
                        unpackFloat1(packedMinOffset),
                        unpackFloat2(packedMinOffset),
                    ),
                    invert = invertLong == 1L
                )
            }
        )
    }
}
