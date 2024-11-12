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

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.composables.ui.skippable
import com.tunjid.demo.common.app.demos.AlignmentInterpolationDemoScreen
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
import com.tunjid.demo.common.app.demos.utilities.PaneSeparator
import com.tunjid.demo.common.app.demos.utilities.isActive
import com.tunjid.demo.common.ui.DemoAppState.Companion.rememberPanedNavHostState
import com.tunjid.treenav.StackNav
import com.tunjid.treenav.compose.PaneState
import com.tunjid.treenav.compose.PanedNavHost
import com.tunjid.treenav.compose.PanedNavHostConfiguration
import com.tunjid.treenav.compose.SavedStatePanedNavHostState
import com.tunjid.treenav.compose.configurations.animatePaneBoundsConfiguration
import com.tunjid.treenav.compose.configurations.paneModifierConfiguration
import com.tunjid.treenav.compose.moveablesharedelement.MovableSharedElementHostState
import com.tunjid.treenav.compose.panedNavHostConfiguration
import com.tunjid.treenav.compose.threepane.ThreePane
import com.tunjid.treenav.compose.threepane.configurations.canAnimateOnStartingFrames
import com.tunjid.treenav.compose.threepane.configurations.threePanedMovableSharedElementConfiguration
import com.tunjid.treenav.compose.threepane.configurations.threePanedNavHostConfiguration
import com.tunjid.treenav.compose.threepane.threePaneListDetailStrategy
import com.tunjid.treenav.current
import com.tunjid.treenav.pop
import com.tunjid.treenav.push

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    val appState = remember { DemoAppState() }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SharedTransitionScope { sharedTransitionModifier ->
            val order = remember {
                listOf(
                    ThreePane.Secondary,
                    ThreePane.Primary,
                )
            }
            val splitLayoutState = remember {
                SplitLayoutState(
                    orientation = Orientation.Horizontal,
                    maxCount = order.size,
                    minSize = 120.dp,
                    keyAtIndex = { index ->
                        val indexDiff = order.size - visibleCount
                        order[index + indexDiff]
                    }
                )
            }
            val movableSharedElementHostState = remember {
                MovableSharedElementHostState(
                    sharedTransitionScope = this,
                    canAnimateOnStartingFrames = PaneState<ThreePane, Screen>::canAnimateOnStartingFrames
                )
            }
            val interactingWithPanes = (0..<splitLayoutState.visibleCount).any {
                appState.paneInteractionSourceAt(it).isActive()
            }
            PanedNavHost(
                modifier = Modifier
                    .fillMaxSize(),
                state = appState.rememberPanedNavHostState {
                    this
                        .paneModifierConfiguration {
                            Modifier.fillMaxSize()
                        }
                        .threePanedNavHostConfiguration(
                            windowWidthState = derivedStateOf {
                                splitLayoutState.size
                            }
                        )
                        .threePanedMovableSharedElementConfiguration(
                            movableSharedElementHostState = movableSharedElementHostState
                        )
                        .animatePaneBoundsConfiguration(
                            lookaheadScope = this@SharedTransitionScope,
                            paneBoundsTransform = {
                                BoundsTransform { _, _ ->
                                    spring<Rect>().skippable {
                                        when (paneState.pane) {
                                            ThreePane.Primary,
                                            ThreePane.TransientPrimary,
                                            ThreePane.Secondary,
                                            ThreePane.Tertiary -> interactingWithPanes

                                            null,
                                            ThreePane.Overlay -> true
                                        }
                                    }
                                }
                            },
                        )
                },
            ) {
                val filteredOrder by remember {
                    derivedStateOf { order.filter { nodeFor(it) != null } }
                }
                splitLayoutState.visibleCount = filteredOrder.size

                SplitLayout(
                    state = splitLayoutState,
                    modifier = Modifier
                        .fillMaxSize()
                            then movableSharedElementHostState.modifier
                            then sharedTransitionModifier,
                    itemSeparators = { paneIndex, offset ->
                        PaneSeparator(
                            splitLayoutState = splitLayoutState,
                            interactionSource = appState.paneInteractionSourceAt(paneIndex),
                            index = paneIndex,
                            xOffset = offset,
                        )
                    },
                    itemContent = { index ->
                        val pane = filteredOrder[index]
                        Destination(pane)
                        if (pane == ThreePane.Primary) Destination(ThreePane.TransientPrimary)
                    }
                )
            }
        }
    }
}

@Stable
class DemoAppState {
    private val navigationState = mutableStateOf(
        StackNav(
            name = "demo app",
            children = listOf(Screen.Demos)
        )
    )
    private val paneInteractionSourceList = mutableStateListOf<MutableInteractionSource>()

    private val panedNavHostConfiguration = demoAppNavHostConfiguration(
        stackNavState = navigationState,
        push = { screen ->
            navigationState.value = navigationState.value.push(screen)
        },
        pop = {
            navigationState.value = navigationState.value.pop()
        },
    )

    fun paneInteractionSourceAt(index: Int): MutableInteractionSource {
        while (paneInteractionSourceList.lastIndex < index) {
            paneInteractionSourceList.add(MutableInteractionSource())
        }
        return paneInteractionSourceList[index]
    }

    companion object {
        @Composable
        fun DemoAppState.rememberPanedNavHostState(
            configurationBlock: PanedNavHostConfiguration<
                    ThreePane,
                    StackNav,
                    Screen
                    >.() -> PanedNavHostConfiguration<ThreePane, StackNav, Screen>
        ): SavedStatePanedNavHostState<ThreePane, Screen> {
            val panedNavHostState = remember {
                SavedStatePanedNavHostState(
                    panes = ThreePane.entries.toList(),
                    configuration = panedNavHostConfiguration.configurationBlock(),
                )
            }
            return panedNavHostState
        }
    }
}

private fun demoAppNavHostConfiguration(
    stackNavState: State<StackNav>,
    push: (Screen) -> Unit,
    pop: () -> Unit,
) = panedNavHostConfiguration(
    navigationState = stackNavState,
    destinationTransform = { multiStackNav ->
        multiStackNav.current as? Screen ?: throw IllegalArgumentException(
            "MultiStackNav leaf node ${multiStackNav.current} must be a Screen"
        )
    },
    strategyTransform = { currentScreen ->
        when (currentScreen) {
            Screen.Demos -> demoAppStrategy {
                DemoSelectionScreen(
                    screen = currentScreen,
                    screens = remember { Screen.entries.filterNot(Screen.Demos::equals) },
                    onScreenSelected = push,
                )
            }

            Screen.LazyGridDemoScreen -> demoAppStrategy {
                LazyGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.LazyListDemoScreen -> demoAppStrategy {
                LazyListDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.LazyStickyHeaderListDemoScreen -> demoAppStrategy {
                LazyStickyHeaderListDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.LazyStickyHeaderGridDemoScreen -> demoAppStrategy {
                LazyStickyHeaderGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.LazyStickyHeaderStaggeredGridDemoScreen -> demoAppStrategy {
                LazyStickyHeaderStaggeredGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.LazyStaggeredGridDemoScreen -> demoAppStrategy {
                LazyStaggeredGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.DragToDismissDemoScreen -> demoAppStrategy {
                DragToDismissDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.AlignmentInterpolationDemoScreen -> demoAppStrategy {
                AlignmentInterpolationDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.ContentScaleInterpolationDemoScreen -> demoAppStrategy {
                ContentScaleInterpolationDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop
                )
            }

            Screen.PointerOffsetScrollStaggeredGridDemoScreen -> demoAppStrategy {
                PointerOffsetLazyStaggeredGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop,
                )
            }

            Screen.PointerOffsetScrollListDemoScreen -> demoAppStrategy {
                PointerOffsetLazyListDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop,
                )
            }

            Screen.PointerOffsetScrollGridDemoScreen -> demoAppStrategy {
                PointerOffsetLazyGridDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop,
                )
            }

            Screen.SplitLayoutDemoScreen -> demoAppStrategy {
                SplitLayoutDemoScreen(
                    screen = currentScreen,
                    onBackPressed = pop,
                )
            }
        }
    }
)

private fun demoAppStrategy(
    demoComposable: @Composable () -> Unit
) = threePaneListDetailStrategy<Screen>(
    paneMapping = { destination ->
        mapOf(
            ThreePane.Primary to destination,
            ThreePane.Secondary to Screen.Demos.takeUnless(destination::equals),
        )
    },
    render = {
        demoComposable()
    },
)
