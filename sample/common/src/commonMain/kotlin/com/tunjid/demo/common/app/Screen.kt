package com.tunjid.demo.common.app

sealed class Screen(
    val name: String
) {

    data object Demos : Screen("Demos")

    data object LazyListDemoScreen: Screen("Lists")

    data object LazyGridDemoScreen: Screen("Grids")

    data object LazyStaggeredGridDemoScreen: Screen("Staggered Grids")

    data object DragToDismissDemoScreen: Screen("Drag To Dismiss")
}
