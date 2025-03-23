package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HorizontalDistribute
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pinch
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SwipeVertical
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toSize
import com.tunjid.composables.gesturezoom.GestureZoomState
import com.tunjid.composables.gesturezoom.GestureZoomState.Companion.gestureZoomable
import com.tunjid.composables.gesturezoom.rememberGestureZoomState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.ui.Screen
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun GestureZoomDemoScreen(
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

        val icons = remember {
            listOf(
                Icons.Default.Pinch,
                Icons.Default.Compress,
                Icons.Default.HorizontalDistribute,
                Icons.Default.SortByAlpha,
                Icons.AutoMirrored.Filled.ListAlt,
                Icons.Default.OpenInFull,
                Icons.Default.SwipeVertical,
                Icons.Default.GridOn,
            )
        }

        HorizontalPager(
            state = rememberPagerState { icons.size }
        ) {
            val zoomState = rememberGestureZoomState(
                options = GestureZoomState.Options(
                    scale = GestureZoomState.Options.Scale.Layout,
                    offset = GestureZoomState.Options.Offset.None,
                )
            )
            val coroutineScope = rememberCoroutineScope()
            var absMaxPanOffset by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .onPlaced { coordinates ->
                            val size = coordinates.size
                            val scaledSize = (size.toSize() * zoomState.zoomScale).toIntSize()
                            absMaxPanOffset = Offset(
                                x = max(0f, ((scaledSize.width - size.width).toFloat() / 2)),
                                y = max(0f, ((scaledSize.height - size.height).toFloat() / 2)),
                            )
                        }
                        .gestureZoomable(zoomState)
                        .offset {
                            with(zoomState.panOffset) {
                                Offset(
                                    x =
                                        if (x < 0) max(x, -absMaxPanOffset.x)
                                        else min(x, absMaxPanOffset.x),
                                    y =
                                        if (y < 0) max(y, -absMaxPanOffset.y)
                                        else min(y, absMaxPanOffset.y),
                                ).round()
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val positiveX = offset.x > zoomState.size.width / 2
                                    val positiveY = offset.y > zoomState.size.height / 2
                                    zoomState.updatePan(
                                        zoomState.panOffset + Offset(
                                            x = if (positiveX) 50f else -50f,
                                            y = if (positiveY) 50f else -50f,
                                        )
                                    )
                                },
                                onDoubleTap = {
                                    coroutineScope.launch {
                                        zoomState.toggleZoom()
                                    }
                                },
                                onLongPress = {
                                    zoomState.updateZoomScale(updatedScale = 1f)
                                }
                            )
                        },
                    contentDescription = null,
                    imageVector = icons[it],
                )
            }
        }
    }
}
