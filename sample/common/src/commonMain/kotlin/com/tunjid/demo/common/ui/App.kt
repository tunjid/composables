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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
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
import com.tunjid.composables.backpreview.BackPreviewState
import com.tunjid.composables.backpreview.backPreview
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.composables.ui.skipIf
import com.tunjid.demo.common.app.demos.utilities.PaneSeparator
import com.tunjid.demo.common.app.demos.utilities.isActive
import com.tunjid.demo.common.ui.AppState.Companion.rememberPanedNavHostState
import com.tunjid.treenav.StackNav
import com.tunjid.treenav.compose.PanedNavHost
import com.tunjid.treenav.compose.PanedNavHostConfiguration
import com.tunjid.treenav.compose.PanedNavHostScope
import com.tunjid.treenav.compose.SavedStatePanedNavHostState
import com.tunjid.treenav.compose.configurations.animatePaneBoundsConfiguration
import com.tunjid.treenav.compose.configurations.paneModifierConfiguration
import com.tunjid.treenav.compose.moveablesharedelement.MovableSharedElementHostState
import com.tunjid.treenav.compose.panedNavHostConfiguration
import com.tunjid.treenav.compose.threepane.ThreePane
import com.tunjid.treenav.compose.threepane.configurations.predictiveBackConfiguration
import com.tunjid.treenav.compose.threepane.configurations.threePanedMovableSharedElementConfiguration
import com.tunjid.treenav.compose.threepane.configurations.threePanedNavHostConfiguration
import com.tunjid.treenav.compose.threepane.threePaneListDetailStrategy
import com.tunjid.treenav.current
import com.tunjid.treenav.pop
import com.tunjid.treenav.push

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App(
    appState: AppState = remember { AppState() }
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SharedTransitionScope { sharedTransitionModifier ->
            val backPreviewSurfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                animateDpAsState(if (appState.isPreviewingBack) 16.dp else 0.dp).value
            )
            val movableSharedElementHostState = remember {
                MovableSharedElementHostState<ThreePane, Screen>(
                    sharedTransitionScope = this,
                )
            }
            val interactingWithPanes = (0..<appState.splitLayoutState.visibleCount).any {
                appState.paneInteractionSourceAt(it).isActive()
            }
            PanedNavHost(
                modifier = Modifier
                    .fillMaxSize(),
                state = appState.rememberPanedNavHostState {
                    this
                        .paneModifierConfiguration {
                            if (paneState.pane == ThreePane.TransientPrimary) Modifier
                                .fillMaxSize()
                                .backPreview(appState.backPreviewState)
                                .background(backPreviewSurfaceColor, RoundedCornerShape(16.dp))
                            else Modifier
                                .fillMaxSize()
                        }
                        .threePanedNavHostConfiguration(
                            windowWidthState = derivedStateOf {
                                appState.splitLayoutState.size
                            }
                        )
                        .predictiveBackConfiguration(
                            isPreviewingBack = derivedStateOf {
                                appState.isPreviewingBack
                            },
                            backPreviewTransform = StackNav::pop,
                        )
                        .threePanedMovableSharedElementConfiguration(
                            movableSharedElementHostState = movableSharedElementHostState
                        )
                        .animatePaneBoundsConfiguration(
                            lookaheadScope = this@SharedTransitionScope,
                            paneBoundsTransform = {
                                BoundsTransform { _, _ ->
                                    spring<Rect>().skipIf {
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
                val filteredPaneOrder by remember {
                    derivedStateOf { appState.filteredPaneOrder(this) }
                }
                appState.splitLayoutState.visibleCount = filteredPaneOrder.size
                SplitLayout(
                    state = appState.splitLayoutState,
                    modifier = Modifier
                        .fillMaxSize()
                            then sharedTransitionModifier,
                    itemSeparators = { paneIndex, offset ->
                        PaneSeparator(
                            splitLayoutState = appState.splitLayoutState,
                            interactionSource = appState.paneInteractionSourceAt(paneIndex),
                            index = paneIndex,
                            xOffset = offset,
                        )
                    },
                    itemContent = { index ->
                        val pane = filteredPaneOrder[index]
                        Destination(pane)
                        if (pane == ThreePane.Primary) Destination(ThreePane.TransientPrimary)
                    }
                )
            }
        }
    }
}

@Stable
class AppState {
    private val navigationState = mutableStateOf(
        StackNav(
            name = "demo app",
            children = listOf(Screen.Demos)
        )
    )
    private val paneInteractionSourceList = mutableStateListOf<MutableInteractionSource>()
    private val paneRenderOrder = listOf(
        ThreePane.Secondary,
        ThreePane.Primary,
    )
    private val panedNavHostConfiguration = demoAppNavHostConfiguration(
        stackNavState = navigationState,
        push = { screen ->
            navigationState.value = navigationState.value.push(screen)
        },
        pop = {
            navigationState.value = navigationState.value.pop()
        },
    )

    val backPreviewState = BackPreviewState()
    val splitLayoutState = SplitLayoutState(
        orientation = Orientation.Horizontal,
        maxCount = paneRenderOrder.size,
        minSize = 10.dp,
        keyAtIndex = { index ->
            val indexDiff = paneRenderOrder.size - visibleCount
            paneRenderOrder[index + indexDiff]
        }
    )

    internal val isPreviewingBack
        get() = !backPreviewState.progress.isNaN()

    fun filteredPaneOrder(
        panedNavHostScope: PanedNavHostScope<ThreePane, Screen>
    ): List<ThreePane> {
        val order = paneRenderOrder.filter { panedNavHostScope.nodeFor(it) != null }
        return order
    }

    fun paneInteractionSourceAt(index: Int): MutableInteractionSource {
        while (paneInteractionSourceList.lastIndex < index) {
            paneInteractionSourceList.add(MutableInteractionSource())
        }
        return paneInteractionSourceList[index]
    }

    fun goBack() {
        navigationState.value = navigationState.value.pop()
    }

    companion object {
        @Composable
        fun AppState.rememberPanedNavHostState(
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
        demoAppStrategy {
            currentScreen.demoUI(currentScreen, push, pop)
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
