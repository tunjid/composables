package com.tunjid.demo.common.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HorizontalDistribute
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.SwipeVertical
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.tunjid.demo.common.app.demos.AccumulatedOffsetNestedScrollConnectionDemoScreen
import com.tunjid.demo.common.app.demos.AlignmentInterpolationDemoScreen
import com.tunjid.demo.common.app.demos.BackPreviewDemoScreen
import com.tunjid.demo.common.app.demos.ConstrainedPlacementDemoScreen
import com.tunjid.demo.common.app.demos.ContentScaleInterpolationDemoScreen
import com.tunjid.demo.common.app.demos.DemoSelectionScreen
import com.tunjid.demo.common.app.demos.DragToDismissDemoScreen
import com.tunjid.demo.common.app.demos.LazyGridDemoScreen
import com.tunjid.demo.common.app.demos.LazyListDemoScreen
import com.tunjid.demo.common.app.demos.LazyStaggeredGridDemoScreen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderGridDemoScreen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderListDemoScreen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderStaggeredGridDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyGridDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyListDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyStaggeredGridDemoScreen
import com.tunjid.demo.common.app.demos.SplitLayoutDemoScreen
import com.tunjid.treenav.Node

enum class Screen(
    val title: String,
    val description: String,
    val icons: List<ImageVector>,
    val demoUI: @Composable (currentScreen: Screen, push: (Screen) -> Unit, pop: () -> Unit) -> Unit,
) : Node {
    Demos(
        title = "Demos",
        description = "A list of the demos in the app.",
        icons = listOf(Icons.Default.TouchApp),
        demoUI = { currentScreen, push, _ ->
            DemoSelectionScreen(
                screen = currentScreen,
                screens = remember {
                    Screen.entries.filterNot(Demos::equals)
                },
                onScreenSelected = push,
            )
        },
    ),
    SplitLayoutDemoScreen(
        title = "Split Layouts",
        description = "Drag dividers to dynamically resize layouts",
        icons = listOf(
            Icons.Default.Swipe,
            Icons.Default.SwipeVertical,
            Icons.Default.SpaceDashboard,
        ),
        demoUI = { currentScreen, _, pop ->
            SplitLayoutDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    DragToDismissDemoScreen(
        title = "Drag To Dismiss",
        description = "A gallery of colors. Drag on the detail view of a color to dismiss it.",
        icons = listOf(
            Icons.Default.Swipe,
            Icons.Default.Close,
        ),
        demoUI = { currentScreen, _, pop ->
            DragToDismissDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    ContentScaleInterpolationDemoScreen(
        title = "ContentScale Interpolation",
        description = "Tap on a ContentScale to animate the change of the beach scene's display in its frame.",
        icons = listOf(
            Icons.Default.AspectRatio,
            Icons.Default.Animation,
        ),
        demoUI = { currentScreen, _, pop ->
            ContentScaleInterpolationDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    AlignmentInterpolationDemoScreen(
        title = "Alignment Interpolation",
        description = "Tap on a rounded rectangle to align the main rounded rectangle to it.",
        icons = listOf(
            Icons.Default.Apps,
            Icons.Default.OpenWith,
            Icons.Default.Animation,
        ),
        demoUI = { currentScreen, _, pop ->
            AlignmentInterpolationDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    BackPreviewDemoScreen(
        title = "Back Preview Modifier",
        description = "Drag from the edges to preview previous navigation destinations according to the material spec.",
        icons = listOf(
            Icons.AutoMirrored.Filled.ArrowBack,
            Icons.Default.Visibility,
        ),
        demoUI = { currentScreen, _, pop ->
            BackPreviewDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    ConstrainedPlacementDemoScreen(
        title = "Constrained Placement Modifier",
        description = "Slide layouts out of frame at a minimum size.",
        icons = listOf(
            Icons.Default.Compress,
            Icons.Default.HorizontalDistribute,
        ),
        demoUI = { currentScreen, _, pop ->
            ConstrainedPlacementDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyStickyHeaderListDemoScreen(
        title = "Sticky Header Lists",
        description = "An alphabetical list of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyStickyHeaderListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyStickyHeaderGridDemoScreen(
        title = "Sticky Header Grids",
        description = "An alphabetical grid of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.GridOn,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyStickyHeaderGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyStickyHeaderStaggeredGridDemoScreen(
        title = "Sticky Header Staggered Grids",
        description = "An alphabetical staggered grid of colors with sticky headers for each letter.",
        icons = listOf(
            Icons.Default.SortByAlpha,
            Icons.Default.SpaceDashboard,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyStickyHeaderStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyListDemoScreen(
        title = "Collapsing Header Lists With Scrollbars",
        description = "A large list of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyGridDemoScreen(
        title = "Collapsing Header Grids With Scrollbars",
        description = "A large grid of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.GridOn,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    LazyStaggeredGridDemoScreen(
        title = "Collapsing Header Staggered Grids With Scrollbars",
        description = "A large staggered list of colors with a collapsing header that can be scrolled with a scrollbar.",
        icons = listOf(
            Icons.Default.OpenInFull,
            Icons.Default.SwipeVertical,
            Icons.Default.SpaceDashboard,
        ),
        demoUI = { currentScreen, _, pop ->
            LazyStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    PointerOffsetScrollListDemoScreen(
        title = "Pointer Offset Scroll List",
        description = "Drag items to transfer their color while scrolling the list.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.AutoMirrored.Filled.ListAlt,
        ),
        demoUI = { currentScreen, _, pop ->
            PointerOffsetLazyListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    PointerOffsetScrollGridDemoScreen(
        title = "Pointer Offset Scroll Grid",
        description = "Drag items to transfer their color while scrolling the grid.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.Default.GridOn,
        ),
        demoUI = { currentScreen, _, pop ->
            PointerOffsetLazyGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    PointerOffsetScrollStaggeredGridDemoScreen(
        title = "Pointer Offset Scroll Staggered Grid",
        description = "Drag items to transfer their color while scrolling the staggered grid.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.OpenWith,
            Icons.Default.SpaceDashboard,
        ),
        demoUI = { currentScreen, _, pop ->
            PointerOffsetLazyStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    AccumulatedOffsetNestedScrollConnectionDemoScreen(
        title = "Accumulated Offset Nested Scroll Connection",
        description = "Scroll and watch UI elements react.",
        icons = listOf(
            Icons.Default.TouchApp,
            Icons.Default.Add,
            Icons.AutoMirrored.Filled.CompareArrows,
        ),
        demoUI = { currentScreen, _, pop ->
            AccumulatedOffsetNestedScrollConnectionDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        },
    ),
    ;

    override val id: String
        get() = title
}
