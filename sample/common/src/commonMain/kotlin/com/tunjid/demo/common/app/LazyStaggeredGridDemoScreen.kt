package com.tunjid.demo.common.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val scrollbarState = staggeredGridState.scrollbarState(
        itemsAvailable = pastelColors.size
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ColorHeader(
            selectedColor = selectedColor,
            onBackPressed = onBackPressed,
        ) {
            LazyVerticalStaggeredGrid(
                state = staggeredGridState,
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 16.dp,
                ),
                columns = StaggeredGridCells.Adaptive(200.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = pastelColors,
                    itemContent = { (name, color) ->
                        ListDemoItem(
                            color = color,
                            name = name,
                            modifier = Modifier
                                .fillMaxSize()
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
                scrollInProgress = staggeredGridState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = staggeredGridState.rememberBasicScrollbarThumbMover()
            )
        }
    }
}

@Composable
private fun ListDemoItem(
    color: Color,
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(100.dp),
                )

        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(text = name)
    }
}