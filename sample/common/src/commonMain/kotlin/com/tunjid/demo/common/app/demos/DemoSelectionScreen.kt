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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.grid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.grid.scrollbarState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.FastScrollbar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun DemoSelectionScreen(
    screen: Screen,
    screens: List<Screen>,
    onScreenSelected: (Screen) -> Unit
) {
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(
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
            LazyVerticalGrid(
                state = gridState,
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 16.dp,
                ),
                columns = GridCells.Adaptive(400.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = screens,
                    itemContent = { screen ->
                        DemoScreenItem(
                            screen = screen,
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
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
                scrollInProgress = gridState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = gridState.rememberBasicScrollbarThumbMover()
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
                    tint = Color.Black,
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

