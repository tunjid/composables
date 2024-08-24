package com.tunjid.demo.common.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.grid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.grid.scrollbarState

@Composable
fun LazyGridDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(pastelColors.first())
    }
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(itemsAvailable = pastelColors.size)
    val density = LocalDensity.current
    val navigationBarInsets = WindowInsets.navigationBars

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoCollapsingHeader(
            title = "Grid collapsing header with scrollbar demo",
            item = selectedItem,
            onBackPressed = onBackPressed,
        ) { collapsedHeight ->
            LazyVerticalGrid(
                state = gridState,
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = pastelColors,
                    itemContent = { item ->
                        GridDemoItem(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
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
                    .width(12.dp)
                    .align(Alignment.TopEnd),
                state = scrollbarState,
                scrollInProgress = gridState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = gridState.rememberBasicScrollbarThumbMover()
            )
        }
    }
}

@Composable
private fun GridDemoItem(
    item: ColorItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(color = item.color)
            )
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = 4.dp,
                        vertical = 2.dp,
                    )
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = item.name,
            )
        }
    }
}