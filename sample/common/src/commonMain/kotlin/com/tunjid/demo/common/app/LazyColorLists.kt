package com.tunjid.demo.common.app

import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tunjid.composables.collapsingheader.CollapsingHeader
import com.tunjid.composables.collapsingheader.CollapsingHeaderState
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState
import kotlin.math.roundToInt

class LazyColorLists {
}


val pastelColors = (0..<9).map {
    listOf(
        Pair("Pastel Pink", Color(0XFFFFD1DC)),
        Pair("Pastel Blue", Color(0XFFB0E0E6)),
        Pair("Pastel Green", Color(0XFF90EE90)),
        Pair("Pastel Yellow", Color(0XFFFFFFE0)),
        Pair("Pastel Purple", Color(0XFFD3B1F7)),
        Pair("Pastel Orange", Color(0XFFFFCC99)),
        Pair("Pastel Peach", Color(0XFFFFE5B4)),
        Pair("Pastel Teal", Color(0XFFAEC6CF)),
        Pair("Pastel Beige", Color(0XFFF5DEB3)),
        Pair("Pastel Lavender", Color(0XFFE6E6FA)),
    )
}.flatten()

@Composable
fun ListCollapsingHeader() {
    var selectedColor by mutableStateOf(pastelColors.first().second)
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(itemsAvailable = pastelColors.size)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ColorHeader(selectedColor) {
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

                        Row(
                            modifier = Modifier.fillParentMaxWidth(),
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
private fun ColorHeader(
    selectedColor: Color,
    listBody: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val headerState = remember {
        CollapsingHeaderState(
            collapsedHeight = with(density) { 56.dp.toPx() },
            initialExpandedHeight = with(density) { 56.dp.toPx() },
            decayAnimationSpec = splineBasedDecay(density)
        )
    }
    CollapsingHeader(
        state = headerState,
        headerContent = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(300.dp)
                    .offset {
                        IntOffset(x = 0, y = -headerState.translation.roundToInt().also { println("T: $it") })
                    }
                    .background(selectedColor)
            )
        },
        body = {
            listBody()
        }
    )
}