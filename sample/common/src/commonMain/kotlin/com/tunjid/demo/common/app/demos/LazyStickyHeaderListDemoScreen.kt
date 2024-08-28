package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.stickyheader.list.StickyHeaderList
import com.tunjid.demo.common.app.ColorItem
import com.tunjid.demo.common.app.distinctPastelColors
import com.tunjid.demo.common.app.groupedPastelColors
import com.tunjid.demo.common.ui.ContentType
import com.tunjid.demo.common.ui.ItemHeader
import com.tunjid.demo.common.ui.ListDemoItem

@Composable
fun LazyStickyHeaderListDemoScreen(
    onBackPressed: () -> Unit,
) {
    var selectedItem by remember {
        mutableStateOf(distinctPastelColors.first())
    }
    val listState = rememberLazyListState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(onBackPressed)
        StickyHeaderList(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            isStickyHeaderItem = {
                it.contentType == ContentType.Header
            },
            stickyHeader = stickyHeader@{ firstItemInfo ->
                ItemHeader(
                    charFor(firstItemInfo = firstItemInfo),
                    selectedItem.color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = "Drag to dismiss demo")
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

private fun charFor(
    firstItemInfo: LazyListItemInfo?
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



