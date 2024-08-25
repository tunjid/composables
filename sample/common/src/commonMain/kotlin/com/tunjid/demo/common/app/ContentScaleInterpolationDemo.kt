package com.tunjid.demo.common.app

//import androidx.compose.ui.graphics.vector.Path

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tunjid.composables.ui.interpolate

@Composable
fun ContentScaleInterpolationDemoScreen(
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(
            onBackPressed = onBackPressed
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            val contentScales = remember {
                listOf(
                    "Fit" to ContentScale.Fit,
                    "Crop" to ContentScale.Crop,
                    "None" to ContentScale.None,
                    "Fill Width" to ContentScale.FillWidth,
                    "Fill Height" to ContentScale.FillHeight,
                    "Fill Bounds" to ContentScale.FillBounds,
                    "Inside" to ContentScale.Inside,
                )
            }
            var selectedContentScale by remember { mutableStateOf(contentScales.first().second) }

            BeachScene(
                contentScale = selectedContentScale
            )
            ContentScaleSelection(
                contentScales = contentScales,
                selectedContentScale = selectedContentScale,
                onContentScaleSelected = { selectedContentScale = it })
        }
    }
}

@Composable
private fun BeachScene(
    contentScale: ContentScale
) {
    Image(
        imageVector = BeachScene,
        contentDescription = null,
        contentScale = contentScale.interpolate(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .aspectRatio(20 / 9f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
            )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContentScaleSelection(
    contentScales: List<Pair<String, ContentScale>>,
    selectedContentScale: ContentScale,
    onContentScaleSelected: (ContentScale) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp,
            ),
        horizontalArrangement = Arrangement.Center,
    ) {
        contentScales.forEach { (name, contentScale) ->
            Text(
                text = name,
                textDecoration = if (contentScale == selectedContentScale) TextDecoration.Underline
                else TextDecoration.None,
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 8.dp,
                )
                    .clickable { onContentScaleSelected(contentScale) }
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
            Text(text = "ContentScale interpolation demo")
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

private val BeachScene: ImageVector = ImageVector.Builder(
    name = "BeachScene",
    defaultWidth = 500.dp,
    defaultHeight = 300.dp,
    viewportWidth = 500f,
    viewportHeight = 300f
).apply {
    skyPath()
    sandPath()
    sunPath()
    beachHutGroup(
        translationX = 50f,
        translationY = 170f,
    )
    beachHutGroup(
        translationX = 200f,
        translationY = 160f,
    )
    beachHutGroup(
        translationX = 350f,
        translationY = 140f,
    )
}.build()

private fun ImageVector.Builder.skyPath() {
    path(
        fill = SolidColor(Color(0xFF87CEEB)),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
    ) {
        moveTo(0f, 0f)
        horizontalLineTo(500f)
        verticalLineTo(300f)
        horizontalLineTo(0f)
        verticalLineTo(0f)
        close()
    }
}

private fun ImageVector.Builder.sandPath() {
    path(
        fill = SolidColor(Color(0xFFF0E68C)),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
    ) {
        moveTo(0f, 200f)
        curveTo(100f, 180f, 200f, 190f, 300f, 170f)
        curveTo(400f, 150f, 500f, 180f, 500f, 200f)
        lineTo(500f, 300f)
        lineTo(0f, 300f)
        close()
    }
}

private fun ImageVector.Builder.sunPath() {
    path(
        fill = SolidColor(Color(0xFFFFFF00)),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
    ) {
        moveTo(460f, 70f)
        arcTo(50f, 50f, 0f, isMoreThanHalf = false, isPositiveArc = true, 410f, 120f)
        arcTo(50f, 50f, 0f, isMoreThanHalf = false, isPositiveArc = true, 360f, 70f)
        arcTo(50f, 50f, 0f, isMoreThanHalf = false, isPositiveArc = true, 460f, 70f)
        close()
    }
}

private fun ImageVector.Builder.beachHutGroup(
    translationX: Float,
    translationY: Float,
) {
    group(
        scaleX = 1f,
        scaleY = 1f,
        translationX = translationX,
        translationY = translationY,
        pivotX = 0f,
        pivotY = 0f,
    ) {
        path(
            fill = SolidColor(Color(0xFF8B4513)),
            fillAlpha = 1.0f,
            stroke = null,
            strokeAlpha = 1.0f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(0f, 0f)
            curveTo(30f, -20f, 60f, -10f, 90f, 0f)
            lineTo(90f, 30f)
            lineTo(0f, 30f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF006400)),
            fillAlpha = 1.0f,
            stroke = null,
            strokeAlpha = 1.0f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(10f, 10f)
            horizontalLineTo(30f)
            verticalLineTo(30f)
            horizontalLineTo(10f)
            verticalLineTo(10f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF006400)),
            fillAlpha = 1.0f,
            stroke = null,
            strokeAlpha = 1.0f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(60f, 10f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 50f, 20f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 40f, 10f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 60f, 10f)
            close()
        }
    }
}

