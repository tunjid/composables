package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.staggeredgrid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.staggeredgrid.scrollbarState
import com.tunjid.demo.common.app.DemoCollapsingHeader
import com.tunjid.demo.common.app.FastScrollbar
import com.tunjid.demo.common.app.pastelColors
import com.tunjid.demo.common.ui.GridDemoItem

@Composable
fun LazyStaggeredGridDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(pastelColors.first())
    }
    val staggeredGridState = rememberLazyStaggeredGridState()
    val scrollbarState = staggeredGridState.scrollbarState(itemsAvailable = pastelColors.size)
    val density = LocalDensity.current
    val navigationBarInsets = WindowInsets.navigationBars

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoCollapsingHeader(
            title = "Staggered grid collapsing header with scrollbar demo",
            item = selectedItem,
            onBackPressed = onBackPressed,
        ) { collapsedHeight ->
            LazyVerticalStaggeredGrid(
                state = staggeredGridState,
                contentPadding = remember(density, navigationBarInsets, collapsedHeight) {
                    WindowInsets(
                        left = 8.dp,
                        right = 8.dp,
                        top = 16.dp,
                        bottom = 16.dp + with(density) { collapsedHeight.toDp() },
                    )
                        .add(navigationBarInsets)
                }
                    .asPaddingValues(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                columns = StaggeredGridCells.Adaptive(100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = pastelColors,
                    itemContent = { item ->
                        GridDemoItem(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(remember {
                                    val step = (-1..4).random() * 2
                                    1f + (step / 10f)
                                })
                                .clickable { selectedItem = item }
                        )
                    }
                )
            }
            FastScrollbar(
                modifier = Modifier
                    .padding(
                        remember(density, navigationBarInsets, collapsedHeight) {
                            WindowInsets(
                                bottom = 16.dp + with(density) { collapsedHeight.toDp() },
                            )
                                .add(navigationBarInsets)
                        }
                            .asPaddingValues()
                    )
                    .fillMaxHeight()
                    .width(12.dp)
                    .align(Alignment.TopEnd),
                state = scrollbarState,
                scrollInProgress = staggeredGridState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = staggeredGridState.rememberBasicScrollbarThumbMover()
            )
        }
    }
}
