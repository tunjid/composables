package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.stickyheader.list.StickyHeaderList
import com.tunjid.demo.common.app.demos.utilities.ColorItem
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.ui.Screen
import com.tunjid.demo.common.app.demos.utilities.distinctPastelColors
import com.tunjid.demo.common.app.demos.utilities.groupedByFirstLetter
import com.tunjid.demo.common.app.demos.utilities.ContentType
import com.tunjid.demo.common.app.demos.utilities.ItemHeader
import com.tunjid.demo.common.app.demos.utilities.ListDemoItem
import com.tunjid.demo.common.app.demos.utilities.charFor

@Composable
fun LazyStickyHeaderListDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    val distinctPastelColors = remember {
        distinctPastelColors()
    }
    val groupedPastelColors = remember(distinctPastelColors) {
        distinctPastelColors.groupedByFirstLetter()
    }
    var selectedItem by remember(distinctPastelColors) {
        mutableStateOf(distinctPastelColors.first())
    }
    val listState = rememberLazyListState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        StickyHeaderList(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            isStickyHeaderItem = {
                it.contentType == ContentType.Header
            },
            stickyHeader = stickyHeader@{ key, contentType ->
                ItemHeader(
                    char = distinctPastelColors.charFor(key, contentType),
                    backgroundColor = selectedItem.color,
                )
            }
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                modifier = Modifier.fillMaxSize()
            ) {
                groupedPastelColors.forEach { (char, items) ->
                    item(
                        key = char.toString(),
                        contentType = ContentType.Header,
                        content = {
                            ItemHeader(
                                char = char,
                                backgroundColor = selectedItem.color
                            )
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
    }
}