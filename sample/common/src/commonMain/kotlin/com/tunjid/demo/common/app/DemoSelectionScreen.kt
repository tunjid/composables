package com.tunjid.demo.common.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState

@Composable
fun DemoSelectionScreen(
    screens: List<Screen>,
    onScreenSelected: (Screen) -> Unit
) {
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(itemsAvailable = pastelColors.size)

    Box(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 16.dp,
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = screens,
                itemContent = { screen ->
                    Text(
                        text = screen.title,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp,
                            )
                            .clickable { onScreenSelected(screen) }
                    )
                }
            )
        }
        FastScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .width(12.dp)
                .align(Alignment.TopEnd),
            state = scrollbarState,
            scrollInProgress = listState.isScrollInProgress,
            orientation = Orientation.Vertical,
            onThumbMoved = listState.rememberBasicScrollbarThumbMover()
        )
    }

}

