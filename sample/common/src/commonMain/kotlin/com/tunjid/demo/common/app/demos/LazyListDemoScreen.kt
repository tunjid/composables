package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState
import com.tunjid.demo.common.app.demos.utilities.DemoCollapsingHeader
import com.tunjid.demo.common.app.demos.utilities.FastScrollbar
import com.tunjid.demo.common.app.demos.utilities.ListDemoItem
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun LazyListDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(pastelColors.first())
    }
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(itemsAvailable = pastelColors.size)


    DemoCollapsingHeader(
        screen = screen,
        item = selectedItem,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            state = listState,
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = pastelColors,
                itemContent = { item ->
                    ListDemoItem(
                        item = item,
                        modifier = Modifier
                            .clickable { selectedItem = item }
                            .fillParentMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp,
                            )
                    )
                }
            )
        }
        FastScrollbar(
            modifier = Modifier
                .padding(
                    WindowInsets.navigationBars.asPaddingValues()
                )
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
