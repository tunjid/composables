package com.tunjid.demo.common.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.SwipeVertical
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val icons: List<ImageVector>,

    ) {
    Demos(
        title = "Demos",
        icons = listOf(Icons.Default.TouchApp),
    ),
    PointerOffsetScrollStaggeredGridDemoScreen(
        title = "Pointer Offset Scroll Staggered Grid",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.Default.SpaceDashboard,
        ),
    ),
    LazyStickyHeaderListDemoScreen(
        title = "Sticky Header Lists",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
    ),
    LazyStickyHeaderGridDemoScreen(
        title = "Sticky Header Grids",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.GridOn,
        ),
    ),
    LazyStickyHeaderStaggeredGridDemoScreen(
        title = "Sticky Header Staggered Grids",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.SpaceDashboard,
        ),
    ),
    LazyListDemoScreen(
        title = "Collapsing Header Lists With Scrollbars",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
    ),
    LazyGridDemoScreen(
        title = "Collapsing Header Grids With Scrollbars",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.GridOn,
        ),
    ),
    LazyStaggeredGridDemoScreen(
        title = "Collapsing Header Staggered Grids With Scrollbars",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.SpaceDashboard,
        ),
    ),
    DragToDismissDemoScreen(
        title = "Drag To Dismiss",
        icons = listOf(
            Icons.Default.Swipe,
            Icons.Default.Close,
        ),
    ),
    AlignmentInterpolationDemoScreen(
        title = "Alignment Interpolation",
        icons = listOf(
            Icons.Default.Apps,
            Icons.Default.OpenWith,
            Icons.Default.Animation,
        ),
    ),
    ContentScaleInterpolationDemoScreen(
        title = "ContentScale Interpolation",
        icons = listOf(
            Icons.Default.AspectRatio,
            Icons.Default.Animation,
        ),
    ),
}
