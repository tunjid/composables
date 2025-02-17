package com.tunjid.demo.common.app.demos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.tunjid.composables.dragtodismiss.DragToDismissState
import com.tunjid.composables.dragtodismiss.dragToDismiss
import com.tunjid.demo.common.app.demos.utilities.ColorItem
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DragToDismissDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    SharedTransitionLayout(
        modifier = Modifier.fillMaxSize()
    ) SharedTransitionLayout@{
        var selectedItem by remember { mutableStateOf<ColorItem?>(null) }
        val dragToDismissState = remember { DragToDismissState() }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DemoTopAppBar(
                screen = screen,
                onBackPressed = onBackPressed
            )
            AnimatedContent(
                targetState = selectedItem,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = { fadeIn().togetherWith(fadeOut()) }
            ) { currentItem ->
                when (currentItem) {
                    null -> ColorSelectionGrid(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        onItemSelected = { selectedItem = it }
                    )

                    else -> DragToDismissContainer(
                        dragToDismissState = dragToDismissState,
                        item = currentItem,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@AnimatedContent,
                        onItemSelected = { selectedItem = it },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ColorSelectionGrid(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onItemSelected: (ColorItem) -> Unit,
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
            key = ColorItem::id,
            itemContent = { item ->
                DemoItem(
                    item = item,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = item.id
                            ),
                            animatedVisibilityScope = animatedContentScope,
                        )
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onItemSelected(item) }
                )
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DragToDismissContainer(
    dragToDismissState: DragToDismissState,
    item: ColorItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onItemSelected: (ColorItem?) -> Unit,
) = with(sharedTransitionScope) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        DemoItem(
            item = item,
            modifier = Modifier
                .offset { dragToDismissState.offset.round() }
                .sharedElement(
                    sharedContentState = sharedTransitionScope.rememberSharedContentState(key = item.id),
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
                        onItemSelected(null)
                    },
                )
                .clickable { onItemSelected(null) }
        )
    }
}

@Composable
private fun DemoItem(
    item: ColorItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = item.color)
        )
    }
}