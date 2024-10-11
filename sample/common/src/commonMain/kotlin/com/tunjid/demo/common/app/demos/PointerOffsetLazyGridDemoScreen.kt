package com.tunjid.demo.common.app.demos

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.tunjid.composables.pointeroffsetscroll.PointerOffsetScrollState
import com.tunjid.composables.pointeroffsetscroll.pointerOffsetScroll
import com.tunjid.demo.common.app.demos.utilities.ColorItem
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.ui.Screen
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.app.demos.utilities.ColorDot
import com.tunjid.demo.common.app.demos.utilities.GridDemoItem

@Composable
fun PointerOffsetLazyGridDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf<ColorItem?>(null)
    }
    val gridState = rememberLazyGridState()
    val pointerOffsetScrollState = remember {
        PointerOffsetScrollState(
            scrollableState = gridState,
            orientation = Orientation.Vertical,
            scrollThresholdFraction = 0.2f,
            scrollAmountMultiplier = 0.2f,
        )
    }

    val mutableColors = remember { pastelColors.toMutableStateList() }
    val indexOfItemUnder: (Offset) -> Int? = remember {
        { offset ->
            gridState.keyAt(offset)
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
            LazyVerticalGrid(
                state = gridState,
                contentPadding = WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
                    .add(WindowInsets.navigationBars)
                    .asPaddingValues(),
                columns = GridCells.Adaptive(100.dp),
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
                                .aspectRatio(1f)
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

private fun LazyGridState.keyAt(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int
