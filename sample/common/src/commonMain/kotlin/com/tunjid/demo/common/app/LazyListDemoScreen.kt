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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState

@Composable
fun LazyListDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedColor by mutableStateOf(pastelColors.first().second)
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(
        itemsAvailable = pastelColors.size
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ColorHeader(
            selectedColor = selectedColor,
            onBackPressed = onBackPressed,
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
                    items = pastelColors,
                    itemContent = { (name, color) ->
                        ListDemoItem(
                            color = color,
                            name = name,
                            modifier = Modifier
                                .fillParentMaxWidth()
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
                scrollInProgress = listState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = listState.rememberBasicScrollbarThumbMover()
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