package com.tunjid.composables.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue

@Stable
internal class Interpolator<T> private constructor(
    value: T,
    private val interpolatedSnapshot: (previous: T, current: T, fraction: Float) -> T,
) {
    var current by mutableStateOf(value)
        private set
    var previous by mutableStateOf(value)
        private set

    var interpolation by mutableFloatStateOf(1f)
        private set

    private fun update(newCurrent: T) {
        if (current != newCurrent) {
            previous = when (interpolation) {
                // Value has changed, trigger an animation
                1f -> current
                // A previous animation has been interrupted. Capture the present state,
                // and restart the animation.
                else -> interpolatedSnapshot(previous, current, interpolation)
            }
            // Reset the interpolation
            interpolation = 0f
        }
        current = newCurrent
    }

    companion object {
        @Composable
        fun <T : Any> rememberUpdatedInterpolator(
            value: T,
            animationSpec: AnimationSpec<Float>,
            interpolatedSnapshot: (previous: T, current: T, fraction: Float) -> T,
        ): Interpolator<T> {
            val updatedAnimationSpec by rememberUpdatedState(animationSpec)

            val interpolator = remember {
                Interpolator(
                    value = value,
                    interpolatedSnapshot = interpolatedSnapshot,
                )
            }.also { it.update(value) }

            LaunchedEffect(interpolator.current) {
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = updatedAnimationSpec,
                    block = { progress, _ ->
                        interpolator.interpolation = progress
                    },
                )
            }
            return interpolator
        }
    }
}

