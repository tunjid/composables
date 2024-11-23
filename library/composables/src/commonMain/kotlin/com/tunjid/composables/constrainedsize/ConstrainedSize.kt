package com.tunjid.composables.constrainedsize

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

/**
 * Constrains a layout to not be smaller than the specified [minSize] around a
 * particular [orientation]. If the layout constrains are smaller than the [minSize] specified,
 * the layout will begin to move out of frame towards the direction specified by [atStart].
 *
 * @param orientation The orientation where the size should be constrained about.
 * @param minSize The minimum size the layout should be before it starts moving out of frame.
 * @param atStart Specifies the direction movement out of frame should proceed in:
 * - [Orientation.Horizontal]: [atStart] of true means shifting towards the start of the layout,
 * else its shifts towards the end.
 * - [Orientation.Vertical]: [atStart] of true means shifting towards the top of the layout,
 * else its shifts towards the bottom.
 */
fun Modifier.constrainedSizePlacement(
    orientation: Orientation,
    minSize: Dp,
    atStart: Boolean,
) = layout { measurable, constraints ->
    val minPaneSize = minSize.roundToPx()
    val actualConstraints = when (orientation) {
        Orientation.Horizontal -> if (constraints.maxWidth >= minPaneSize) constraints
        else constraints.copy(maxWidth = minPaneSize)

        Orientation.Vertical -> if (constraints.maxHeight >= minPaneSize) constraints
        else constraints.copy(maxHeight = minPaneSize)
    }
    val placeable = measurable.measure(actualConstraints)

    layout(width = placeable.width, height = placeable.height) {
        when (orientation) {
            Orientation.Horizontal -> placeable.placeRelative(
                x = if (constraints.maxWidth >= minPaneSize) 0
                else when {
                    atStart -> constraints.maxWidth - minPaneSize
                    else -> minPaneSize - constraints.maxWidth
                },
                y = 0,
            )

            Orientation.Vertical -> placeable.placeRelative(
                x = 0,
                y = if (constraints.maxHeight >= minPaneSize) 0
                else when {
                    atStart -> constraints.maxHeight - minPaneSize
                    else -> minPaneSize - constraints.maxHeight
                },
            )
        }
    }
}
