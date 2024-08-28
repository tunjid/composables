package com.tunjid.demo.common.app

enum class Screen(
    val title: String
) {
    Demos("Demos"),
    LazyStickyHeaderListDemoScreen("Sticky Header Lists"),
    LazyStickyHeaderGridDemoScreen("Sticky Header Grids"),
    LazyStickyHeaderStaggeredGridDemoScreen("Sticky Header Staggered Grids"),
    LazyListDemoScreen("Collapsing Header Lists With Scrollbars"),
    LazyGridDemoScreen("Collapsing Header Grids With Scrollbars"),
    LazyStaggeredGridDemoScreen("Collapsing Header Staggered Grids With Scrollbars"),
    DragToDismissDemoScreen("Drag To Dismiss"),
    AlignmentInterpolationDemoScreen("Alignment Interpolation"),
    ContentScaleInterpolationDemoScreen("ContentScale Interpolation"),
}
