package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.tunjid.composables.pointeroffsetscroll.PointerOffsetScrollState
import com.tunjid.composables.pointeroffsetscroll.pointerOffsetScroll
import com.tunjid.demo.common.app.ColorItem
import com.tunjid.demo.common.app.DemoTopAppBar
import com.tunjid.demo.common.app.Screen
import com.tunjid.demo.common.app.pastelColors
import com.tunjid.demo.common.ui.GridDemoItem

@Composable
fun PointerOffsetLazyStaggeredGridDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf<ColorItem?>(null)
    }
    val staggeredGridState = rememberLazyStaggeredGridState()
    val pointerOffsetScrollState = remember {
        PointerOffsetScrollState(
            scrollableState = staggeredGridState,
            orientation = Orientation.Vertical,
            scrollThresholdFraction = 0.8f,
        )
    }

    val mutableColors = remember { pastelColors.toMutableStateList() }
    val indexOfItemUnder: (Offset) -> Int? = remember {
        { offset ->
            staggeredGridState.keyAt(offset)
                ?.let { id -> mutableColors.binarySearch { it.id - id } }
                ?.takeIf { it >= 0 }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerOffsetScroll(pointerOffsetScrollState)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { dragStart ->
                            selectedItem = indexOfItemUnder(dragStart)?.let(mutableColors::get)
                            if (selectedItem != null) pointerOffsetScrollState.currentOffset =
                                dragStart
                        },
                        onDragEnd = {
                            selectedItem?.let { item ->
                                indexOfItemUnder(pointerOffsetScrollState.currentOffset)
                                    ?.let {
                                        mutableColors[it] = mutableColors[it].copy(
                                            name = item.name,
                                            color = item.color,
                                        )
                                    }
                            }
                            selectedItem = null
                            pointerOffsetScrollState.currentOffset = Offset.Unspecified
                        },
                        onDragCancel = {
                            selectedItem = null
                            pointerOffsetScrollState.currentOffset = Offset.Unspecified
                        },
                        onDrag = { change, _ ->
                            if (selectedItem == null) return@detectDragGesturesAfterLongPress
                            pointerOffsetScrollState.currentOffset = change.position
                        }
                    )
                }
        ) {
            LazyVerticalStaggeredGrid(
                state = staggeredGridState,
                contentPadding = WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
                    .add(WindowInsets.navigationBars)
                    .asPaddingValues(),
                columns = StaggeredGridCells.Adaptive(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = mutableColors,
                    key = ColorItem::id,
                    itemContent = { item ->
                        GridDemoItem(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(remember {
                                    val step = (-1..4).random() * 2
                                    1f + (step / 10f)
                                })
                        )
                    }
                )
            }

            ColorDot(
                color = selectedItem?.color,
                offset = pointerOffsetScrollState.currentOffset,
            )
        }
    }
}

@Composable
private fun ColorDot(
    color: Color?,
    offset: Offset,
) {
    val currentColor = color ?: return

    Box(
        modifier = Modifier
            .size(56.dp)
            .offset((-28).dp, (-28).dp)
            .offset { offset.round() }
            .background(
                color = currentColor,
                shape = CircleShape,
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
            )
    )
}

fun LazyStaggeredGridState.keyAt(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int
