package com.tunjid.demo.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tunjid.demo.common.app.ColorItem

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

sealed class ContentType {
    data object Header : ContentType()
    data object Item : ContentType()
}