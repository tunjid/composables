package com.tunjid.demo.common.app

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tunjid.composables.collapsingheader.CollapsingHeader
import com.tunjid.composables.collapsingheader.CollapsingHeaderState
import kotlin.math.max
import kotlin.math.roundToInt

data class ColorItem(
    val name: String,
    val color: Color,
    val id: Int,
)

val pastelColors = (0..<9).map { index ->
    listOf(
        ColorItem("Baby Pink", Color(0xFFF4C2C2), index),
        ColorItem("Lavender", Color(0xFFE6E6FA), index),
        ColorItem("Mint Green", Color(0xFF98FB98), index),
        ColorItem("Pale Yellow", Color(0xFFFFFFE0), index),
        ColorItem("Baby Blue", Color(0xFF89CFF0), index),
        ColorItem("Peach", Color(0xFFFFDAB9), index),
        ColorItem("Seafoam Green", Color(0xFFB2FFFF), index),
        ColorItem("Butter Yellow", Color(0xFFFFFF99), index),
        ColorItem("Lilac", Color(0xFFC8A2C8), index),
        ColorItem("Sky Blue", Color(0xFF87CEEB), index),
        ColorItem("Coral Pink", Color(0xFFF88379), index),
        ColorItem("Pistachio", Color(0xFFBEF574), index),
        ColorItem("Periwinkle", Color(0xFFCCCCFF), index),
        ColorItem("Cream", Color(0xFFFFFDD0), index),
        ColorItem("Dusky Pink", Color(0xFFCC8899), index),
        ColorItem("Pale Green", Color(0xFF90EE90), index),
        ColorItem("Light Aqua", Color(0xFF93FFE8), index),
        ColorItem("Vanilla", Color(0xFFF3E5AB), index),
        ColorItem("Mauve", Color(0xFFE0B0FF), index),
        ColorItem("Powder Blue", Color(0xFFB0E0E6), index),
        ColorItem("Blush Pink", Color(0xFFFFB6C1), index),
        ColorItem("Celadon", Color(0xFFACE1AF), index),
        ColorItem("Wisteria", Color(0xFFC9A0DC), index),
        ColorItem("Eggshell", Color(0xFFF0EAD6), index),
        ColorItem("Misty Rose", Color(0xFFFFE4E1), index),
    )
}
    .flatMapIndexed { index: Int, items: List<ColorItem> ->
        items.mapIndexed { innerIndex, item ->
            item.copy(id = (index * items.size) + innerIndex)
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DemoCollapsingHeader(
    title: String,
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
                        .background(animatedColor)
                ) {
                    Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars))
                    Spacer(Modifier.height(300.dp))
                }
                TopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackPressed,
                            content = {
                                Image(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.onSizeChanged {
                        headerState.collapsedHeight = it.height.toFloat()
                    },
                )
            }
        },
        body = {
            body(collapsedHeight)
        }
    )
}