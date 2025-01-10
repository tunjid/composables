package com.tunjid.demo.common.app.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.tunjid.composables.accumulatedoffsetnestedscrollConnection.rememberAccumulatedOffsetNestedScrollConnection
import com.tunjid.demo.common.app.demos.utilities.ListDemoItem
import com.tunjid.demo.common.app.demos.utilities.pastelColors
import com.tunjid.demo.common.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccumulatedOffsetNestedScrollConnectionDemoScreen(
    screen: Screen,
    onBackPressed: () -> Unit,
) {
    val statusBarInsets = WindowInsets.statusBars
    val topAppBarOffsetNestedScrollConnection =
        rememberAccumulatedOffsetNestedScrollConnection(
            maxOffset = { Offset.Zero },
            minOffset = maxOffset@{
                Offset(
                    x = 0f,
                    y = -statusBarInsets.run {
                        getTop(this@maxOffset) + getBottom(this@maxOffset)
                    } - ToolbarHeight.toPx()
                )
            },
        )

    val navigationBarInsets = WindowInsets.navigationBars
    val bottomNavAccumulatedOffsetNestedScrollConnection =
        rememberAccumulatedOffsetNestedScrollConnection(
            invert = true,
            maxOffset = maxOffset@{
                Offset(
                    x = 0f,
                    y = navigationBarInsets.run {
                        getTop(this@maxOffset) + getBottom(this@maxOffset)
                    } + BottomNavHeight.toPx()
                )
            },
            minOffset = { Offset.Zero },
        )


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarOffsetNestedScrollConnection)
            .nestedScroll(bottomNavAccumulatedOffsetNestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.offset {
                    topAppBarOffsetNestedScrollConnection.offset.round()
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    )
                },
                title = {
                    Text(text = screen.title)
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = paddingValues,
            ) {
                items(
                    items = pastelColors,
                    itemContent = { item ->
                        ListDemoItem(
                            item = item,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp,
                                )
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.offset {
                    bottomNavAccumulatedOffsetNestedScrollConnection.offset.round()
                },
                onClick = {},
                content = {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Button"
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.offset {
                    bottomNavAccumulatedOffsetNestedScrollConnection.offset.round()
                },
                content = {

                },
            )
        }
    )
}

val ToolbarHeight = 64.dp
val BottomNavHeight = 80.dp