package com.tunjid.demo.common.app.demos.utilities

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tunjid.composables.collapsingheader.CollapsingHeaderLayout
import com.tunjid.composables.collapsingheader.CollapsingHeaderState
import com.tunjid.composables.collapsingheader.CollapsingHeaderStatus
import com.tunjid.demo.common.ui.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun DemoCollapsingHeader(
    screen: Screen,
    item: ColorItem,
    onBackPressed: () -> Unit,
    body: @Composable (collapsedHeight: Float) -> Unit,
) {
    val density = LocalDensity.current
    val collapsedHeight = with(density) { 56.dp.toPx() } +
            WindowInsets.statusBars.getTop(density).toFloat() +
            WindowInsets.statusBars.getBottom(density).toFloat()
    val headerState = remember {
        CollapsingHeaderState(
            collapsedHeight = collapsedHeight,
            initialExpandedHeight = with(density) { 400.dp.toPx() },
            decayAnimationSpec = splineBasedDecay(density)
        )
    }
    val animatedColor by animateColorAsState(
        item.color.copy(alpha = max(1f - headerState.progress, 0.6f))
    )
    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    CollapsingHeaderLayout(
        state = headerState,
        headerContent = {
            Box(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        scope.launch {
                            headerState.animateTo(
                                if (headerState.progress > 0.5f) CollapsingHeaderStatus.Expanded
                                else CollapsingHeaderStatus.Collapsed
                            )
                        }
                    },
                    onDoubleClick = {
                        scope.launch {
                            headerState.snapTo(
                                if (headerState.progress > 0.5f) CollapsingHeaderStatus.Expanded
                                else CollapsingHeaderStatus.Collapsed
                            )
                        }
                    },
                )
            ) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -headerState.translation.roundToInt()
                            )
                        }
                        .background(animatedColor)
                )
                DemoTopAppBar(
                    screen = screen,
                    onBackPressed = onBackPressed,
                    modifier = Modifier.onSizeChanged {
                        headerState.collapsedHeight = it.height.toFloat()
                    }
                )
            }
        },
        body = {
            body(collapsedHeight)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoTopAppBar(
    screen: Screen,
    onBackPressed: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = screen.title)
        },
        navigationIcon = {
            if (onBackPressed != null) IconButton(
                onClick = onBackPressed,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        actions = actions,
        modifier = modifier,
    )
}