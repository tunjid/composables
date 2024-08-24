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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.staggeredgrid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.staggeredgrid.scrollbarState

@Composable
fun LazyStaggeredGridDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedColor by remember {
        mutableStateOf(pastelColors.first().second)
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
            selectedColor = selectedColor,
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
                    itemContent = { (name, color) ->
                        StaggeredGridDemoItem(
                            color = color,
                            name = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedColor = color }
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

@Composable
private fun StaggeredGridDemoItem(
    color: Color,
    name: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(remember {
                        val step = (0..5).random() * 2
                        1f + (step / 10f)
                    })
                    .background(color = color)
            )
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = 4.dp,
                        vertical = 2.dp,
                    )
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = name,
            )
        }
    }
}