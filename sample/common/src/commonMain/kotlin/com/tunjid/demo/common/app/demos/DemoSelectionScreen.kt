package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.FastScrollbar
import com.tunjid.demo.common.ui.Screen
import com.tunjid.demo.common.app.demos.utilities.pastelColors

@Composable
fun DemoSelectionScreen(
    screen: Screen,
    screens: List<Screen>,
    onScreenSelected: (Screen) -> Unit
) {
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(
        itemsAvailable = screens.size
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
        )
        Box(
            modifier = Modifier.fillMaxSize()
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
                        DemoScreenItem(
                            screen = screen,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                )
                                .clickable { onScreenSelected(screen) },
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DemoScreenItem(
    screen: Screen,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        FlowRow(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = remember { pastelColors.random().color },
                    shape = RoundedCornerShape(60.dp)
                )
                .padding(4.dp)
                .rotate(
                    if (screen.icons.size == 2) 45f
                    else 0f
                ),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
        ) {
            screen.icons.forEach { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(horizontal = 2.dp)
                        .rotate(
                            if (screen.icons.size == 2) -45f
                            else 0f
                        )
                )
            }
        }
        Column {
            Text(
                text = screen.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )
            Text(
                text = screen.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
        }
    }
}

