package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.grid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.grid.scrollbarState
import com.tunjid.demo.common.app.demos.utilities.DemoCollapsingHeader
import com.tunjid.demo.common.app.demos.utilities.FastScrollbar
import com.tunjid.demo.common.app.demos.utilities.GridDemoItem
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun LazyGridDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(pastelColors.first())
    }
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(itemsAvailable = pastelColors.size)

    DemoCollapsingHeader(
        screen = screen,
        item = selectedItem,
        onBackPressed = onBackPressed,
    ) {
        LazyVerticalGrid(
            state = gridState,
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
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
                            .aspectRatio(1f)
                            .clickable { selectedItem = item }
                    )
                }
            )
        }
        FastScrollbar(
            modifier = Modifier
                .padding(
                    WindowInsets.navigationBars.asPaddingValues()
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
