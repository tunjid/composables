package com.tunjid.composables.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.lerp

/**
 * Creates a [Alignment] instance that interpolates the result of
 * [Alignment.align] as the value of [this] changes. This allows for preserving
 * visual continuity across dynamic contexts like shared elements.
 *
 * @param animationSpec the animation spec used to animate the [IntOffset] returned by
 * [Alignment.align] as it changes.
 */
@Composable
fun Alignment.interpolate(
    animationSpec: AnimationSpec<Float> = spring(),
): Alignment {
    var interpolation by remember {
        mutableFloatStateOf(1f)
    }
    var previousAlignment by remember {
        mutableStateOf(this)
    }
    val currentAlignment by remember {
        mutableStateOf(this)
    }.apply {
        if (value != this@interpolate) previousAlignment = when {
            interpolation == 1f -> value
            else -> CapturedAlignment(
                capturedInterpolation = interpolation,
                previousAlignment = previousAlignment,
                currentAlignment = value
            )
        }.also { interpolation = 0f }
        value = this@interpolate
    }

    LaunchedEffect(currentAlignment) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = animationSpec,
            block = { progress, _ ->
                interpolation = progress
            },
        )
    }

    return remember {
        Alignment { size, space, layoutDirection ->
            val start = previousAlignment.align(
                size = size,
                space = space,
                layoutDirection = layoutDirection,
            )
            val stop = currentAlignment.align(
                size = size,
                space = space,
                layoutDirection = layoutDirection,
            )

            if (start == stop) stop
            else lerp(
                start = start,
                stop = stop,
                fraction = interpolation
            )
        }
    }
}

private class CapturedAlignment(
    private val capturedInterpolation: Float,
    private val previousAlignment: Alignment,
    private val currentAlignment: Alignment,
) : Alignment {

    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection
    ): IntOffset = lerp(
        start = previousAlignment.align(
            size = size,
            space = space,
            layoutDirection = layoutDirection,
        ),
        stop = currentAlignment.align(
            size = size,
            space = space,
            layoutDirection = layoutDirection,
        ),
        fraction = capturedInterpolation
    )
}