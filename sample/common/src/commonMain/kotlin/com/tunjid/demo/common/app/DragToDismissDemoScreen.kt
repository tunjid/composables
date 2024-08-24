package com.tunjid.demo.common.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.tunjid.composables.dragtodismiss.DragToDismissState
import com.tunjid.composables.dragtodismiss.dragToDismiss

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DragToDismissDemoScreen(
    onBackPressed: () -> Unit,
) {
    SharedTransitionLayout(
        modifier = Modifier.fillMaxSize()
    ) SharedTransitionLayout@{
        var selectedColor by remember { mutableStateOf<Color?>(null) }
        val dragToDismissState = remember { DragToDismissState() }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(
                selectedColor = selectedColor,
                onBackPressed = onBackPressed,
                onSelectedColorCleared = { selectedColor = null },
            )
            AnimatedContent(
                targetState = selectedColor,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = { fadeIn().togetherWith(fadeOut()) }
            ) { currentColor ->
                when (currentColor) {
                    null -> ColorSelectionGrid(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        onColorSelected = { selectedColor = it }
                    )

                    else -> DragToDismissContainer(
                        dragToDismissState = dragToDismissState,
                        color = currentColor,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        onColorSelected = { selectedColor = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    selectedColor: Color?,
    onBackPressed: () -> Unit,
    onSelectedColorCleared: () -> Unit,
) {
    Spacer(
        Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
    )
    IconButton(
        onClick = {
            if (selectedColor != null) onSelectedColorCleared()
            else onBackPressed()
        },
        content = {
            Image(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ColorSelectionGrid(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onColorSelected: (Color) -> Unit,
) = with(sharedTransitionScope) {
    LazyVerticalGrid(
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Adaptive(100.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = pastelColors,
            itemContent = { (_, color) ->
                DemoItem(
                    color = color,
                    modifier = Modifier
                        .sharedElement(
                            state = sharedTransitionScope.rememberSharedContentState(
                                key = color
                            ),
                            animatedVisibilityScope = animatedContentScope,
                        )
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onColorSelected(color) }
                )
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DragToDismissContainer(
    dragToDismissState: DragToDismissState,
    color: Color,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onColorSelected: (Color?) -> Unit,
) = with(sharedTransitionScope) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        DemoItem(
            color = color,
            modifier = Modifier
                .offset { dragToDismissState.offset.round() }
                .sharedElement(
                    state = sharedTransitionScope.rememberSharedContentState(key = color),
                    animatedVisibilityScope = animatedContentScope,
                )
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .dragToDismiss(
                    state = dragToDismissState,
                    dragThresholdCheck = { offset, _ ->
                        offset.getDistanceSquared() > with(density) {
                            240.dp.toPx().let { it * it }
                        }
                    },
                    onDismissed = {
                        onColorSelected(null)
                    },
                )
                .clickable { onColorSelected(null) }
        )
    }
}

@Composable
private fun DemoItem(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color)
        )
    }
}