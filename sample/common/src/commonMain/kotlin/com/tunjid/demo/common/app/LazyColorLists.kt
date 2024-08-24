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
        Pair("Baby Pink", Color(0xFFF4C2C2)),
        Pair("Lavender", Color(0xFFE6E6FA)),
        Pair("Mint Green", Color(0xFF98FB98)),
        Pair("Pale Yellow", Color(0xFFFFFFE0)),
        Pair("Baby Blue", Color(0xFF89CFF0)),
        Pair("Peach", Color(0xFFFFDAB9)),
        Pair("Seafoam Green", Color(0xFFB2FFFF)),
        Pair("Butter Yellow", Color(0xFFFFFF99)),
        Pair("Lilac", Color(0xFFC8A2C8)),
        Pair("Sky Blue", Color(0xFF87CEEB)),
        Pair("Coral Pink", Color(0xFFF88379)),
        Pair("Pistachio", Color(0xFFBEF574)),
        Pair("Periwinkle", Color(0xFFCCCCFF)),
        Pair("Cream", Color(0xFFFFFDD0)),
        Pair("Dusky Pink", Color(0xFFCC8899)),
        Pair("Pale Green", Color(0xFF90EE90)),
        Pair("Light Aqua", Color(0xFF93FFE8)),
        Pair("Vanilla", Color(0xFFF3E5AB)),
        Pair("Mauve", Color(0xFFE0B0FF)),
        Pair("Powder Blue", Color(0xFFB0E0E6)),
        Pair("Blush Pink", Color(0xFFFFB6C1)),
        Pair("Celadon", Color(0xFFACE1AF)),
        Pair("Wisteria", Color(0xFFC9A0DC)),
        Pair("Eggshell", Color(0xFFF0EAD6)),
        Pair("Misty Rose", Color(0xFFFFE4E1)),
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