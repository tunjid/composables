package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tunjid.composables.ui.animate
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@Composable
fun ArrangementInterpolationDemoScreen(
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
        val horizontalArrangements = remember {
            listOf(
                "Start" to Arrangement.Start,
                "Center" to Arrangement.Center,
                "End" to Arrangement.End,
                "Between" to Arrangement.SpaceBetween,
                "Around" to Arrangement.SpaceAround,
                "Evenly" to Arrangement.SpaceEvenly,
            )
        }
        val verticalArrangements = remember {
            listOf(
                "Top" to Arrangement.Top,
                "Center" to Arrangement.Center,
                "Bottom" to Arrangement.Bottom,
                "Between" to Arrangement.SpaceBetween,
                "Around" to Arrangement.SpaceAround,
                "Evenly" to Arrangement.SpaceEvenly,
            )
        }

        var horizontalArrangement by remember {
            mutableStateOf(horizontalArrangements.first().second)
        }
        var verticalArrangement by remember {
            mutableStateOf(verticalArrangements.first().second)
        }

        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalValues(
                selected = horizontalArrangement,
                horizontalArrangements = horizontalArrangements,
                onArrangementSelected = { horizontalArrangement = it }
            )
            Row {
                VerticalValues(
                    selected = verticalArrangement,
                    verticalArrangements = verticalArrangements,
                    onArrangementSelected = { verticalArrangement = it },
                )
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalArrangement = verticalArrangement.animate(),
                ) {
                    repeat(4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = horizontalArrangement.animate()
                        ) {
                            (0..4).forEach {
                                ColoredBox(index = it)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable

private fun VerticalValues(
    selected: Arrangement.Vertical,
    verticalArrangements: List<Pair<String, Arrangement.Vertical>>,
    onArrangementSelected: (Arrangement.Vertical) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        verticalArrangements.forEach {
            Text(
                modifier = Modifier.clickable { onArrangementSelected(it.second) },
                text = it.first,
                textDecoration =
                    if (it.second == selected) TextDecoration.Underline
                    else TextDecoration.None,
            )
        }
    }
}

@Composable
private fun HorizontalValues(
    selected: Arrangement.Horizontal,
    horizontalArrangements: List<Pair<String, Arrangement.Horizontal>>,
    onArrangementSelected: (Arrangement.Horizontal) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        horizontalArrangements.forEach {
            Text(
                modifier = Modifier.clickable { onArrangementSelected(it.second) },
                text = it.first,
                textDecoration =
                    if (it.second == selected) TextDecoration.Underline
                    else TextDecoration.None,
            )
        }
    }
}

@Composable
private fun ColoredBox(
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(pastelColors[index].color, RoundedCornerShape(8.dp))
            .size(40.dp)
    )
}