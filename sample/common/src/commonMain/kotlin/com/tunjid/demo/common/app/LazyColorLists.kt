package com.tunjid.demo.common.app

import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tunjid.composables.collapsingheader.CollapsingHeader
import com.tunjid.composables.collapsingheader.CollapsingHeaderState
import kotlin.math.roundToInt

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
internal fun ColorHeader(
    selectedColor: Color,
    onBackPressed: () -> Unit,
    listBody: @Composable () -> Unit,
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
    CollapsingHeader(
        state = headerState,
        headerContent = {
            Box {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -headerState.translation.roundToInt()
                            )
                        }
                        .background(selectedColor)
                ) {
                    Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars))
                    Spacer(Modifier.height(300.dp))
                }
                Column {
                    Spacer(
                        Modifier
                            .windowInsetsPadding(WindowInsets.statusBars)
                    )
                    IconButton(
                        onClick = onBackPressed,
                        content = {
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        body = {
            listBody()
        }
    )
}