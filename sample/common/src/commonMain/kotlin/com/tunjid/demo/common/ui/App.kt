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
import com.tunjid.demo.common.app.demos.LazyStickyHeaderListDemoScreen

@Composable
fun App() {
    val navStack = remember { mutableStateListOf(Screen.Demos) }
    val pop: () -> Unit = remember { { navStack.removeLast() } }

    AnimatedContent(navStack.last()) { currentScreen ->
        when (currentScreen) {
            Screen.Demos -> DemoSelectionScreen(
                screens = remember { Screen.entries },
                onScreenSelected = navStack::add,
            )

            Screen.LazyGridDemoScreen -> LazyGridDemoScreen(pop)
            Screen.LazyListDemoScreen -> LazyListDemoScreen(pop)
            Screen.LazyStickyHeaderListDemoScreen -> LazyStickyHeaderListDemoScreen(pop)
            Screen.LazyStaggeredGridDemoScreen -> LazyStaggeredGridDemoScreen(pop)
            Screen.DragToDismissDemoScreen -> DragToDismissDemoScreen(pop)
            Screen.AlignmentInterpolationDemoScreen -> AlignmentInterpolationDemoScreen(pop)
            Screen.ContentScaleInterpolationDemoScreen -> ContentScaleInterpolationDemoScreen(pop)
        }
    }
}
