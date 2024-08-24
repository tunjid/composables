package com.tunjid.demo.common.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
    ) {
        var selectedColor by remember { mutableStateOf<Color?>(null) }
        val dragToDismissState = remember { DragToDismissState() }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(
                Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
            IconButton(
                onClick = onBackPressed,
                content = {
                    Image(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            )
            AnimatedContent(
                targetState = selectedColor,
                modifier = Modifier.fillMaxSize()
            ) { color ->
                when (color) {
                    null -> LazyVerticalGrid(
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
                                        .fillMaxWidth()
                                        .clickable { selectedColor = color }
                                        .sharedElement(
                                            state = rememberSharedContentState(key = color),
                                            animatedVisibilityScope = this@AnimatedContent,
                                        )
                                )
                            }
                        )
                    }

                    else -> Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .offset { dragToDismissState.offset.round() }
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(color)
                                .dragToDismiss(
                                    state = dragToDismissState,
                                    dragThresholdCheck = { offset, _ ->
                                        offset.getDistanceSquared() > (500 * 500)
                                    },
                                    onDismissed = {
                                        selectedColor = null
                                    },
                                )
//                                .clickable { selectedColor = null }
                                .sharedElement(
                                    state = rememberSharedContentState(key = color),
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun DemoItem(
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(color = color)
        )
    }
}