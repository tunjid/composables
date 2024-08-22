package com.tunjid.demo.common.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.grid.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.grid.scrollbarState

@Composable
fun LazyGridDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedColor by remember {
        mutableStateOf(pastelColors.first().second)
    }
    val gridState = rememberLazyGridState()
    val scrollbarState = gridState.scrollbarState(
        itemsAvailable = pastelColors.size
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ColorHeader(
            selectedColor = selectedColor,
            onBackPressed = onBackPressed,
        ) {
            LazyVerticalGrid(
                state = gridState,
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 16.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = pastelColors,
                    itemContent = { (name, color) ->
                        GridDemoItem(
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

@Composable
private fun GridDemoItem(
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
                    .aspectRatio(1f)
                    .background(
                        color = color,
                    )

            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 4.dp,
                    vertical = 2.dp,
                ),
                text = name,
            )
        }
    }
}