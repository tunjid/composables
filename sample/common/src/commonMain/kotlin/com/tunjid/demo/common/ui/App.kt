/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tunjid.demo.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.tunjid.demo.common.app.demos.AlignmentInterpolationDemoScreen
import com.tunjid.demo.common.app.demos.ContentScaleInterpolationDemoScreen
import com.tunjid.demo.common.app.demos.DemoSelectionScreen
import com.tunjid.demo.common.app.demos.DragToDismissDemoScreen
import com.tunjid.demo.common.app.demos.LazyGridDemoScreen
import com.tunjid.demo.common.app.demos.LazyListDemoScreen
import com.tunjid.demo.common.app.demos.LazyStaggeredGridDemoScreen
import com.tunjid.demo.common.app.Screen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderGridDemoScreen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderListDemoScreen
import com.tunjid.demo.common.app.demos.LazyStickyHeaderStaggeredGridDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyGridDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyListDemoScreen
import com.tunjid.demo.common.app.demos.PointerOffsetLazyStaggeredGridDemoScreen

@Composable
fun App() {
    val navStack = remember { mutableStateListOf(Screen.Demos) }
    val pop: () -> Unit = remember { { navStack.removeLast() } }

    AnimatedContent(navStack.last()) { currentScreen ->
        when (currentScreen) {
            Screen.Demos -> DemoSelectionScreen(
                screen = currentScreen,
                screens = remember { Screen.entries.filterNot(Screen.Demos::equals) },
                onScreenSelected = navStack::add,
            )

            Screen.LazyGridDemoScreen -> LazyGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.LazyListDemoScreen -> LazyListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.LazyStickyHeaderListDemoScreen -> LazyStickyHeaderListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.LazyStickyHeaderGridDemoScreen -> LazyStickyHeaderGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.LazyStickyHeaderStaggeredGridDemoScreen -> LazyStickyHeaderStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.LazyStaggeredGridDemoScreen -> LazyStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.DragToDismissDemoScreen -> DragToDismissDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.AlignmentInterpolationDemoScreen -> AlignmentInterpolationDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.ContentScaleInterpolationDemoScreen -> ContentScaleInterpolationDemoScreen(
                screen = currentScreen,
                onBackPressed = pop
            )

            Screen.PointerOffsetScrollStaggeredGridDemoScreen -> PointerOffsetLazyStaggeredGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )

            Screen.PointerOffsetScrollListDemoScreen -> PointerOffsetLazyListDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )

            Screen.PointerOffsetScrollGridDemoScreen -> PointerOffsetLazyGridDemoScreen(
                screen = currentScreen,
                onBackPressed = pop,
            )
        }
    }
}
