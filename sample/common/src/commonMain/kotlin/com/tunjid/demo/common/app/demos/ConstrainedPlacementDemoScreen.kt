package com.tunjid.demo.common.app.demos

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tunjid.composables.constrainedsize.constrainedSizePlacement
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun ConstrainedPlacementDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var orientation by remember { mutableStateOf(Orientation.Vertical) }
            AnimatedContent(
                targetState = orientation,
                modifier = Modifier
                    .fillMaxWidth(0.625f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            ) { selectedOrientation ->
                val splitLayoutState = remember {
                    SplitLayoutState(
                        orientation = selectedOrientation,
                        maxCount = 2,
                        minSize = 1.dp,
                    )
                }
                SplitLayout(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = splitLayoutState,
                    itemSeparators = { innerIndex, offset ->
                        PaneSeparator(
                            splitLayoutState = splitLayoutState,
                            index = innerIndex,
                            offset = offset,
                        )
                    },
                ) { paneIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .constrainedSizePlacement(
                                orientation = selectedOrientation,
                                minSize = splitLayoutState.size / 4,
                                atStart = paneIndex == 0,
                            )
                            .background(pastelColors[paneIndex].color)
                            .clickable {
                                orientation = when (selectedOrientation) {
                                    Orientation.Vertical -> Orientation.Horizontal
                                    Orientation.Horizontal -> Orientation.Vertical
                                }
                            }
                    )
                }

            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(PaneSeparatorTouchTargetWidthDp, MaterialTheme.colorScheme.onSurface)
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
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            .hoverable(interactionSource)
            .clickable {
                (0..1).forEach {
                    splitLayoutState.setWeightAt(it, 0.5f)
                }
            }
    )
}

private val PaneSeparatorTouchTargetWidthDp = 16.dp
