package com.tunjid.composables.ui

import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Allows for immediately skipping a [FiniteAnimationSpec] to the end when [shouldSkipToEnd]
 * returns true. Returning false allows for the animation to proceed as usual from where it
 * would originally be.
 *
 * Skipping is abrupt, and ideally should be set before the animation begins running, or before
 * its next run.
 *
 * @param shouldSkipToEnd returning true allows for skipping the animation to the end even if
 * the animation is still running.
 */
@Stable
fun <T> FiniteAnimationSpec<T>.skippable(
    shouldSkipToEnd: () -> Boolean,
): FiniteAnimationSpec<T> = SkipSpec(
    backing = this,
    shouldSkipToEnd = shouldSkipToEnd,
)

@Immutable
private class SkipSpec<T>(
    private val backing: FiniteAnimationSpec<T>,
    private val shouldSkipToEnd: () -> Boolean,
) : FiniteAnimationSpec<T> {
    override fun <V : AnimationVector> vectorize(
        converter: TwoWayConverter<T, V>
    ): VectorizedFiniteAnimationSpec<V> = SkipVectorizedFiniteAnimationSpec(
        backing = backing.vectorize(converter),
        shouldSkipToEnd = shouldSkipToEnd,
    )
}

@Immutable
private class SkipVectorizedFiniteAnimationSpec<V : AnimationVector>(
    private val backing: VectorizedFiniteAnimationSpec<V>,
    private val shouldSkipToEnd: () -> Boolean,
) : VectorizedFiniteAnimationSpec<V> {

    override fun getDurationNanos(
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): Long = backing.getDurationNanos(
        initialValue = initialValue,
        targetValue = targetValue,
        initialVelocity = initialVelocity,
    )

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return if (shouldSkipToEnd()) targetValue
        else backing.getValueFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity,
        )
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        return if (shouldSkipToEnd()) targetValue
        else backing.getVelocityFromNanos(
            playTimeNanos = playTimeNanos,
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = initialVelocity,
        )
    }
}