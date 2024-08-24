package com.tunjid.demo.common.app

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tunjid.composables.ui.interpolate

@Composable
fun AlignmentInterpolationDemoScreen(
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(
            onBackPressed = onBackPressed
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            val alignments = remember {
                listOf(
                    Alignment.TopStart,
                    Alignment.TopEnd,
                    Alignment.TopCenter,
                    Alignment.BottomStart,
                    Alignment.BottomEnd,
                    Alignment.BottomCenter,
                    Alignment.CenterStart,
                    Alignment.CenterEnd,
                    Alignment.Center,
                )
            }

            var selectedAlignment by remember { mutableStateOf(alignments.first()) }

            alignments.forEach { alignment ->
                ColoredBox(
                    color = pastelColors.first().color,
                    modifier = Modifier
                        .align(alignment)
                        .padding(40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedAlignment = alignment }
                )
            }
            ColoredBox(
                color = pastelColors.last().color,
                modifier = Modifier
                    .align(
                        selectedAlignment.interpolate(
                            spring(
                                dampingRatio = Spring.DampingRatioHighBouncy,
                                stiffness = Spring.StiffnessVeryLow,
                            )
                        )
                    )
                    .padding(40.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = "Alignment interpolation demo")
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
        }
    )
}

@Composable
private fun ColoredBox(
    color: Color,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
    )
}