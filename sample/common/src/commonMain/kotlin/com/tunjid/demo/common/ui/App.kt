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
import com.tunjid.demo.common.app.DemoSelectionScreen
import com.tunjid.demo.common.app.LazyGridDemoScreen
import com.tunjid.demo.common.app.LazyListDemoScreen
import com.tunjid.demo.common.app.LazyStaggeredGridDemoScreen
import com.tunjid.demo.common.app.Screen

@Composable
fun App() {
    val navStack = remember { mutableStateListOf<Screen>(Screen.Demos) }
    val pop: () -> Unit = remember { { navStack.removeLast() } }

    AnimatedContent(navStack.last()) { currentScreen ->
        when (currentScreen) {
            is Screen.Demos -> DemoSelectionScreen(
                screens = remember {
                    listOf(
                        Screen.LazyListDemoScreen,
                        Screen.LazyGridDemoScreen,
                        Screen.LazyStaggeredGridDemoScreen,
                    )
                },
                onScreenSelected = navStack::add,
            )

            is Screen.LazyGridDemoScreen -> LazyGridDemoScreen(pop)
            is Screen.LazyListDemoScreen -> LazyListDemoScreen(pop)
            is Screen.LazyStaggeredGridDemoScreen -> LazyStaggeredGridDemoScreen(pop)
        }
    }
}
