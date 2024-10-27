package com.tunjid.demo.common.ui

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
    val description: String,
    val icons: List<ImageVector>,
) {
    Demos(
        title = "Demos",
        description = "A list of the demos in the app.",
        icons = listOf(Icons.Default.TouchApp),
    ),
    SplitLayoutDemoScreen(
        title = "Split Layouts",
        description = "Drag dividers to dynamically resize layouts",
        icons = listOf(
            Icons.Default.Swipe,
            Icons.Default.SwipeVertical,
            Icons.Default.SpaceDashboard,
        ),
    ),
    DragToDismissDemoScreen(
        title = "Drag To Dismiss",
        description = "A gallery of colors. Drag on the detail view of a color to dismiss it.",
        icons = listOf(
            Icons.Default.Swipe,
            Icons.Default.Close,
        ),
    ),
    ContentScaleInterpolationDemoScreen(
        title = "ContentScale Interpolation",
        description = "Tap on a ContentScale to animate the change of the beach scene's display in its frame.",
        icons = listOf(
            Icons.Default.AspectRatio,
            Icons.Default.Animation,
        ),
    ),
    AlignmentInterpolationDemoScreen(
        title = "Alignment Interpolation",
        description = "Tap on a rounded rectangle to align the main rounded rectangle to it.",
        icons = listOf(
            Icons.Default.Apps,
            Icons.Default.OpenWith,
            Icons.Default.Animation,
        ),
    ),
    LazyStickyHeaderListDemoScreen(
        title = "Sticky Header Lists",
        description = "An alphabetical list of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
    ),
    LazyStickyHeaderGridDemoScreen(
        title = "Sticky Header Grids",
        description = "An alphabetical grid of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.GridOn,
        ),
    ),
    LazyStickyHeaderStaggeredGridDemoScreen(
        title = "Sticky Header Staggered Grids",
        description = "An alphabetical staggered grid of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.SpaceDashboard,
        ),
    ),
    LazyListDemoScreen(
        title = "Collapsing Header Lists With Scrollbars",
        description = "A large list of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
    ),
    LazyGridDemoScreen(
        title = "Collapsing Header Grids With Scrollbars",
        description = "A large grid of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.GridOn,
        ),
    ),
    LazyStaggeredGridDemoScreen(
        title = "Collapsing Header Staggered Grids With Scrollbars",
        description = "A large staggered list of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.SpaceDashboard,
        ),
    ),
    PointerOffsetScrollListDemoScreen(
        title = "Pointer Offset Scroll List",
        description = "Drag items to transfer their color while scrolling the list.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
    ),
    PointerOffsetScrollGridDemoScreen(
        title = "Pointer Offset Scroll Grid",
        description = "Drag items to transfer their color while scrolling the grid.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.Default.GridOn,
        ),
    ),
    PointerOffsetScrollStaggeredGridDemoScreen(
        title = "Pointer Offset Scroll Staggered Grid",
        description = "Drag items to transfer their color while scrolling the staggered grid.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.Default.SpaceDashboard,
        ),
    ),
}
