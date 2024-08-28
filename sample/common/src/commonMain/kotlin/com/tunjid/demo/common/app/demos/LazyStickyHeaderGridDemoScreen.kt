package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.stickyheader.grid.StickyHeaderGrid
import com.tunjid.demo.common.app.ColorItem
import com.tunjid.demo.common.app.DemoTopAppBar
import com.tunjid.demo.common.app.Screen
import com.tunjid.demo.common.app.distinctPastelColors
import com.tunjid.demo.common.app.groupedPastelColors
import com.tunjid.demo.common.ui.ContentType
import com.tunjid.demo.common.ui.GridDemoItem
import com.tunjid.demo.common.ui.ItemHeader

@Composable
fun LazyStickyHeaderGridDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(distinctPastelColors.first())
    }
    val gridState = rememberLazyGridState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        StickyHeaderGrid(
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            isStickyHeaderItem = {
                it.contentType == ContentType.Header
            },
            stickyHeader = stickyHeader@{ firstItemInfo ->
                ItemHeader(
                    char = charFor(firstItemInfo = firstItemInfo),
                    backgroundColor = selectedItem.color,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        ) {
            LazyVerticalGrid(
                state = gridState,
                contentPadding = WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
                    .add(WindowInsets.navigationBars)
                    .asPaddingValues(),
                columns = GridCells.Adaptive(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                groupedPastelColors.forEach { (char, items) ->
                    item(
                        key = char.toString(),
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = ContentType.Header,
                        content = {
                            ItemHeader(
                                char = char,
                                backgroundColor = selectedItem.color,
                            )
                        },
                    )
                    items(
                        items = items,
                        key = ColorItem::id,
                        contentType = { ContentType.Item },
                        itemContent = { item ->
                            GridDemoItem(
                                item = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickable { selectedItem = item }
                            )
                        }
                    )
                }
            }

        }
    }
}

private fun charFor(
    firstItemInfo: LazyGridItemInfo?
): Char {
    return when (firstItemInfo?.contentType) {
        is ContentType.Header -> {
            val headerItemKey = firstItemInfo.key as? String
            headerItemKey?.first() ?: '-'
        }

        is ContentType.Item -> {
            val headerItemKey = firstItemInfo.key as? Int
                ?: return '-'
            val headerItemIndex = distinctPastelColors
                .binarySearch { it.id - headerItemKey }
            if (headerItemIndex >= 0) distinctPastelColors[headerItemIndex].name
                .first()
                .uppercaseChar()
            else '-'
        }

        else -> '-'
    }
}



