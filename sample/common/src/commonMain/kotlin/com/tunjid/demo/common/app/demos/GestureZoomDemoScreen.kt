package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.tunjid.composables.gesturezoom.GestureZoomState.Companion.gestureZoomable
import com.tunjid.composables.gesturezoom.rememberGestureZoomState
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.ui.Screen
import kotlinx.coroutines.launch

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
            val zoomState = rememberGestureZoomState()
            val coroutineScope = rememberCoroutineScope()
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .gestureZoomable(zoomState)
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
