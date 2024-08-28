package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tunjid.composables.scrollbars.scrollable.list.rememberBasicScrollbarThumbMover
import com.tunjid.composables.scrollbars.scrollable.list.scrollbarState
import com.tunjid.composables.stickyheader.list.StickyHeaderList
import com.tunjid.demo.common.app.ColorItem
import com.tunjid.demo.common.app.DemoCollapsingHeader
import com.tunjid.demo.common.app.FastScrollbar
import com.tunjid.demo.common.app.pastelColors

@Composable
fun LazyStickyHeaderListDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(distinctPastelColors.first())
    }
    val listState = rememberLazyListState()
    val scrollbarState = listState.scrollbarState(itemsAvailable = distinctPastelColors.size)
    val density = LocalDensity.current
    val navigationBarInsets = WindowInsets.navigationBars

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoCollapsingHeader(
            title = "List collapsing header with scrollbar demo",
            item = selectedItem,
            onBackPressed = onBackPressed,
        ) { collapsedHeight ->
            StickyHeaderList(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                isStickyHeaderItem = {
                    it.contentType == ContentType.Header
                },
                stickyHeader = stickyHeader@{ firstItemInfo ->
                    StickyHeader(firstItemInfo)
                }
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = remember(density, navigationBarInsets, collapsedHeight) {
                        WindowInsets(
                            bottom = 16.dp + with(density) { collapsedHeight.toDp() },
                        )
                            .add(navigationBarInsets)
                    }
                        .asPaddingValues(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedPastelColors.forEach { (letter, items) ->
                        item(
                            key = letter.toString(),
                            contentType = ContentType.Header,
                            content = {
                                Header(letter)
                            },
                        )
                        items(
                            items = items,
                            key = ColorItem::id,
                            contentType = { ContentType.Item },
                            itemContent = { item ->
                                ListDemoItem(
                                    item = item,
                                    modifier = Modifier
                                        .clickable { selectedItem = item }
                                        .fillParentMaxWidth()
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp,
                                        )
                                )
                            }
                        )
                    }
                }
            }
            FastScrollbar(
                modifier = Modifier
                    .padding(
                        remember(density, navigationBarInsets, collapsedHeight) {
                            WindowInsets(
                                bottom = 16.dp + with(density) { collapsedHeight.toDp() },
                            )
                                .add(navigationBarInsets)
                        }
                            .asPaddingValues()
                    )
                    .fillMaxHeight()
                    .width(12.dp)
                    .align(Alignment.TopEnd),
                state = scrollbarState,
                scrollInProgress = listState.isScrollInProgress,
                orientation = Orientation.Vertical,
                onThumbMoved = listState.rememberBasicScrollbarThumbMover()
            )
        }
    }
}

@Composable
private fun Header(letter: Char) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            )
    ) {
        Text(text = letter.toString())
    }
}

@Composable
private fun StickyHeader(firstItemInfo: LazyListItemInfo?) {
    when (firstItemInfo?.contentType) {
        is ContentType.Header -> {
            val headerItemKey = firstItemInfo.key as? String
            Header(letter = headerItemKey?.first() ?: '-')
        }

        is ContentType.Item -> {
            val headerItemKey = firstItemInfo.key as? Int
                ?: return Header(letter = '-')
            val headerItemIndex = distinctPastelColors
                .binarySearch { headerItemKey - it.id }
            if (headerItemIndex >= 0) Header(
                letter = distinctPastelColors[headerItemIndex].name.first()
                    .uppercaseChar()
            )
            else Header(letter = '-')
        }
    }
}

@Composable
private fun ListDemoItem(
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

sealed class ContentType {
    data object Header : ContentType()
    data object Item : ContentType()
}

private val distinctPastelColors = pastelColors
    .distinctBy(ColorItem::name)
    .sortedBy(ColorItem::name)
    .mapIndexed { index, colorItem ->  colorItem.copy(id = index) }

private val groupedPastelColors = distinctPastelColors
    .groupBy { it.name.first().uppercaseChar() }
    .mapValues { (_, items) ->
        items.sortedBy(ColorItem::name)
    }