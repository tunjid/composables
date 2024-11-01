package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen
import kotlin.math.max

@Composable
fun SplitLayoutDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var columnCount by remember { mutableIntStateOf(SPLIT_COUNT) }
    var rowCount by remember { mutableIntStateOf(SPLIT_COUNT) }
    val columnKeys = remember { mutableStateListOf(0, 1, 2) }
    val rowKeys = remember { mutableStateListOf(0, 1, 2) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed,
            actions = {
                AssistChip(
                    label = { Text("Rows: $rowCount") },
                    onClick = { rowCount = toggleSplitCount(rowCount) },
                )
                Spacer(Modifier.size(16.dp))
                AssistChip(
                    label = { Text("Columns: $columnCount") },
                    onClick = { columnCount = toggleSplitCount(columnCount) },
                )
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val columnSplitLayoutState = remember {
                SplitLayoutState(
                    maxCount = SPLIT_COUNT,
                    orientation = Orientation.Horizontal,
                    keyAtIndex = { columnKeys[it] },
                )
            }
            LaunchedEffect(columnCount) {
                columnSplitLayoutState.visibleCount = columnCount
            }
            SplitLayout(
                state = columnSplitLayoutState,
                modifier = Modifier.fillMaxSize(),
                itemSeparators = { outerIndex, offset ->
                    PaneSeparator(
                        splitLayoutState = columnSplitLayoutState,
                        index = outerIndex,
                        offset = offset,
                    )
                },
                itemContent = { columnIndex ->
                    val rowSplitLayoutState = remember {
                        SplitLayoutState(
                            maxCount = SPLIT_COUNT,
                            orientation = Orientation.Vertical,
                            keyAtIndex = { rowKeys[it] },
                        )
                    }
                    LaunchedEffect(rowCount) {
                        rowSplitLayoutState.visibleCount = rowCount
                    }
                    SplitLayout(
                        state = rowSplitLayoutState,
                        modifier = Modifier.fillMaxSize(),
                        itemSeparators = { innerIndex, offset ->
                            PaneSeparator(
                                splitLayoutState = rowSplitLayoutState,
                                index = innerIndex,
                                offset = offset,
                            )
                        },
                        itemContent = { rowIndex ->
                            var clickCount by remember { mutableIntStateOf(0) }
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = pickColor(
                                            outerIndex = columnKeys[columnIndex],
                                            innerIndex = rowKeys[rowIndex],
                                        )
                                    )
                                    .fillMaxSize()
                                    .clickable(
                                        indication = ripple(),
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        onClick = {
                                            ++clickCount
                                            columnKeys.shuffle()
                                            rowKeys.shuffle()
                                        },
                                    )
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                    text = "$clickCount",
                                    color = Color.Black,
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun PaneSeparator(
    splitLayoutState: SplitLayoutState,
    modifier: Modifier = Modifier,
    index: Int,
    offset: Dp,
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val draggableState = rememberDraggableState {
        splitLayoutState.dragBy(
            index = index,
            delta = with(density) { it.toDp() }
        )
    }
    Box(
        modifier = modifier
            .run {
                when (splitLayoutState.orientation) {
                    Orientation.Vertical ->
                        offset(y = offset - (PaneSeparatorTouchTargetWidthDp / 2))
                            .height(PaneSeparatorTouchTargetWidthDp)
                            .fillMaxWidth()

                    Orientation.Horizontal ->
                        offset(x = offset - (PaneSeparatorTouchTargetWidthDp / 2))
                            .width(PaneSeparatorTouchTargetWidthDp)
                            .fillMaxHeight()
                }
            }
            .draggable(
                state = draggableState,
                orientation = splitLayoutState.orientation,
                interactionSource = interactionSource,
            )
            .background(MaterialTheme.colorScheme.onSurface)
            .hoverable(interactionSource)
            .clickable {
                (0..1).forEach {
                    splitLayoutState.setWeightAt(it, 0.5f)
                }
            }
    )
}

private fun pickColor(
    outerIndex: Int,
    innerIndex: Int
) = pastelColors[((outerIndex * 2) + (innerIndex * 3)) % pastelColors.size].color

private fun toggleSplitCount(value: Int) = max(
    a = 2,
    b = (value + 1) % (SPLIT_COUNT + 1),
)

private val PaneSeparatorTouchTargetWidthDp = 16.dp

private const val SPLIT_COUNT = 3
