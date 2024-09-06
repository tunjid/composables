package com.tunjid.demo.common.app.demos.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

@Composable
fun ListDemoItem(
    item: ColorItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = item.color,
                    shape = RoundedCornerShape(100.dp),
                )

        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(text = item.name)
    }
}

@Composable
fun GridDemoItem(
    item: ColorItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = item.color)
            )
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = 4.dp,
                        vertical = 2.dp,
                    )
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = item.name,
            )
        }
    }
}

@Composable
fun ItemHeader(
    char: Char,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            )
    ) {
        Text(text = char.toString())
    }
}

@Composable
fun ColorDot(
    color: Color?,
    offset: Offset,
) {
    val currentColor = color ?: return
    Box(
        modifier = Modifier
            .size(56.dp)
            .offset((-28).dp, (-28).dp)
            .offset { offset.round() }
            .background(
                color = currentColor,
                shape = CircleShape,
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
            )
    )
}

fun List<ColorItem>.charFor(
    key: Any?,
    contentType: Any?
): Char = when (contentType) {
    is ContentType.Header -> {
        val headerItemKey = key as? String
        headerItemKey?.first() ?: '-'
    }

    is ContentType.Item -> (key as? Int)?.let { headerItemKey ->
        val headerItemIndex = binarySearch { it.id - headerItemKey }
        if (headerItemIndex >= 0) this[headerItemIndex].name
            .first()
            .uppercaseChar()
        else '-'
    } ?: '-'

    else -> '-'
}

sealed class ContentType {
    data object Header : ContentType()
    data object Item : ContentType()
}
