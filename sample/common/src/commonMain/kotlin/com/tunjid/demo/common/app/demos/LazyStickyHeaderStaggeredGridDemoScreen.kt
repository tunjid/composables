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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tunjid.composables.stickyheader.staggeredgrid.StickyHeaderStaggeredGrid
import com.tunjid.demo.common.app.demos.utilities.ColorItem
import com.tunjid.demo.common.app.demos.utilities.ContentType
import com.tunjid.demo.common.app.demos.utilities.DemoTopAppBar
import com.tunjid.demo.common.app.demos.utilities.GridDemoItem
import com.tunjid.demo.common.app.demos.utilities.ItemHeader
import com.tunjid.demo.common.app.demos.utilities.charFor
import com.tunjid.demo.common.app.demos.utilities.distinctPastelColors
import com.tunjid.demo.common.app.demos.utilities.groupedByFirstLetter
import com.tunjid.demo.common.ui.Screen

@Composable
fun LazyStickyHeaderStaggeredGridDemoScreen(
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
    val staggeredGridState = rememberLazyStaggeredGridState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DemoTopAppBar(
            screen = screen,
            onBackPressed = onBackPressed
        )
        StickyHeaderStaggeredGrid(
            state = staggeredGridState,
            modifier = Modifier.fillMaxSize(),
            isStickyHeaderItem = {
                it.contentType == ContentType.Header
            },
            stickyHeader = stickyHeader@{ _, key, contentType ->
                ItemHeader(
                    char = distinctPastelColors.charFor(key, contentType),
                    backgroundColor = selectedItem.color,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        ) {
            LazyVerticalStaggeredGrid(
                state = staggeredGridState,
                contentPadding = WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
                    .add(WindowInsets.navigationBars)
                    .asPaddingValues(),
                columns = StaggeredGridCells.Adaptive(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                groupedPastelColors.forEach { (char, items) ->
                    item(
                        key = char.toString(),
                        span = StaggeredGridItemSpan.FullLine,
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
                            GridDemoItem(
                                item = item,
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(remember {
                                        val step = (-1..4).random() * 2
                                        1f + (step / 10f)
                                    })
                                    .clickable { selectedItem = item }
                            )
                        }
                    )
                }
            }

        }
    }
}
