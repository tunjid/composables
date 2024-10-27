package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.tunjid.composables.pointeroffsetscroll.PointerOffsetScrollState
import com.tunjid.composables.pointeroffsetscroll.pointerOffsetScroll
import com.tunjid.demo.common.app.demos.utilities.ColorDot
import com.tunjid.demo.common.app.demos.utilities.ColorItem
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.ListDemoItem
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun PointerOffsetLazyListDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf<ColorItem?>(null)
    }
    val listState = rememberLazyListState()
    val pointerOffsetScrollState = remember {
        PointerOffsetScrollState(
            scrollableState = listState,
            orientation = Orientation.Vertical,
            scrollThresholdFraction = 0.2f,
            scrollAmountMultiplier = 0.2f,
        )
    }

    val mutableColors = remember { pastelColors.toMutableStateList() }
    val indexOfItemUnder: (Offset) -> Int? = remember {
        { offset ->
            listState.keyAt(offset)
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
            LazyColumn(
                state = listState,
                contentPadding = WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
                    .add(WindowInsets.navigationBars)
                    .asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = mutableColors,
                    key = ColorItem::id,
                    itemContent = { item ->
                        ListDemoItem(
                            item = item,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp,
                                )
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

private fun LazyListState.keyAt(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        IntSize(width = Int.MAX_VALUE, height = itemInfo.size).toIntRect()
            .contains(hitPoint.round() - IntOffset(x = 0, y = itemInfo.offset))
    }?.key as? Int
