package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.stickyheader.list.StickyHeaderList
import com.tunjid.demo.common.app.ColorItem
import com.tunjid.demo.common.app.Screen
import com.tunjid.demo.common.app.distinctPastelColors
import com.tunjid.demo.common.app.groupedPastelColors

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
                ItemStickyHeader(firstItemInfo)
            }
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                modifier = Modifier.fillMaxSize()
            ) {
                groupedPastelColors.forEach { (letter, items) ->
                    item(
                        key = letter.toString(),
                        contentType = ContentType.Header,
                        content = {
                            ItemHeader(letter)
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

@Composable
private fun ItemHeader(letter: Char) {
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
private fun ItemStickyHeader(firstItemInfo: LazyListItemInfo?) {
    when (firstItemInfo?.contentType) {
        is ContentType.Header -> {
            val headerItemKey = firstItemInfo.key as? String
            ItemHeader(letter = headerItemKey?.first() ?: '-')
        }

        is ContentType.Item -> {
            val headerItemKey = firstItemInfo.key as? Int
                ?: return ItemHeader(letter = '-')
            val headerItemIndex = distinctPastelColors
                .binarySearch { it.id - headerItemKey }
            if (headerItemIndex >= 0) ItemHeader(
                letter = distinctPastelColors[headerItemIndex].name.first()
                    .uppercaseChar()
            )
            else ItemHeader(letter = '-')
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


