package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.fastRoundToInt
import com.tunjid.composables.backpreview.BackPreviewState
import com.tunjid.composables.backpreview.backPreview
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun BackPreviewDemoScreen(
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
        var layoutSize by remember { mutableStateOf(IntSize.Zero) }
        val backPreviewState = remember { BackPreviewState() }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { layoutSize = it }
                    .backPreview(state = backPreviewState)
                    .background(pastelColors.first().color)
            )
            MockPredictiveBackGestureArea(
                backPreviewState = backPreviewState,
                layoutSize = layoutSize,
                reverseDirection = false,
            )
            MockPredictiveBackGestureArea(
                backPreviewState = backPreviewState,
                layoutSize = layoutSize,
                reverseDirection = true,
            )
        }
    }
}

@Composable
private fun BoxScope.MockPredictiveBackGestureArea(
    backPreviewState: BackPreviewState,
    layoutSize: IntSize,
    reverseDirection: Boolean,
) {
    Box(
        modifier = Modifier
            .align(
                if (reverseDirection) Alignment.TopEnd
                else Alignment.TopStart
            )
            .fillMaxHeight()
            .width(24.dp)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState {
                    backPreviewState.pointerOffset += IntOffset(
                        x = it.fastRoundToInt(),
                        y = 0,
                    )
                    backPreviewState.progress =
                        backPreviewState.pointerOffset.x / layoutSize.width.toFloat()
                },
                onDragStarted = {
                    backPreviewState.atStart = !reverseDirection
                    backPreviewState.pointerOffset = it.round()
                },
                onDragStopped = {
                    backPreviewState.progress = Float.NaN
                },
                reverseDirection = reverseDirection,
            )
    )
}

