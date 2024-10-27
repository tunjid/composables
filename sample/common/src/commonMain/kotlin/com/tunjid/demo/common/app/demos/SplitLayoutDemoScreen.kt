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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun SplitLayoutDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val horizontalSplitLayoutState = remember {
                SplitLayoutState(
                    maxCount = 2,
                    orientation = Orientation.Horizontal,
                )
            }
            SplitLayout(
                state = horizontalSplitLayoutState,
                modifier = Modifier.fillMaxSize(),
                itemSeparators = { outerIndex, offset ->
                    PaneSeparator(
                        splitLayoutState = horizontalSplitLayoutState,
                        index = outerIndex,
                        offset = offset,
                    )
                },
                itemContent = { outerIndex ->
                    val verticalSplitLayoutState = remember {
                        SplitLayoutState(
                            maxCount = 2,
                            orientation = Orientation.Vertical,
                        )
                    }
                    SplitLayout(
                        state = verticalSplitLayoutState,
                        modifier = Modifier.fillMaxSize(),
                        itemSeparators = { innerIndex, offset ->
                            PaneSeparator(
                                splitLayoutState = verticalSplitLayoutState,
                                index = innerIndex,
                                offset = offset,
                            )
                        },
                        itemContent = { innerIndex ->
                            Box(
                                modifier = Modifier
                                    .background(color = pickColor(outerIndex, innerIndex))
                                    .fillMaxSize()
                            )
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

val PaneSeparatorTouchTargetWidthDp = 16.dp
