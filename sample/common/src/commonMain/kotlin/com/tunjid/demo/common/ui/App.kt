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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tunjid.composables.backpreview.BackPreviewState
import com.tunjid.composables.backpreview.backPreview
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.demo.common.app.demos.utilities.isActive
import com.tunjid.demo.common.ui.AppState.Companion.rememberMultiPaneDisplayState
import com.tunjid.treenav.StackNav
import com.tunjid.treenav.backStack
import com.tunjid.treenav.compose.MultiPaneDisplay
import com.tunjid.treenav.compose.MultiPaneDisplayScope
import com.tunjid.treenav.compose.MultiPaneDisplayState
import com.tunjid.treenav.compose.moveablesharedelement.MovableSharedElementHostState
import com.tunjid.treenav.compose.threepane.ThreePane
import com.tunjid.treenav.compose.threepane.threePaneEntry
import com.tunjid.treenav.compose.threepane.transforms.backPreviewTransform
import com.tunjid.treenav.compose.threepane.transforms.threePanedAdaptiveTransform
import com.tunjid.treenav.compose.threepane.transforms.threePanedMovableSharedElementTransform
import com.tunjid.treenav.compose.transforms.Transform
import com.tunjid.treenav.compose.transforms.paneModifierTransform
import com.tunjid.treenav.current
import com.tunjid.treenav.pop
import com.tunjid.treenav.push

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App(
    appState: AppState = remember { AppState() },
) {
    Scaffold {
        CompositionLocalProvider(
            LocalAppState provides appState,
        ) {
            SharedTransitionLayout(Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val movableSharedElementHostState = remember {
                    MovableSharedElementHostState<ThreePane, Screen>(
                        sharedTransitionScope = this
                    )
                }
                MultiPaneDisplay(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = appState.rememberMultiPaneDisplayState(
                        remember {
                            listOf(
                                threePanedAdaptiveTransform(
                                    secondaryPaneBreakPoint = mutableStateOf(
                                        SecondaryPaneMinWidthBreakpointDp
                                    ),
                                    tertiaryPaneBreakPoint = mutableStateOf(
                                        TertiaryPaneMinWidthBreakpointDp
                                    ),
                                    windowWidthState = derivedStateOf {
                                        appState.splitLayoutState.size
                                    }
                                ),
                                backPreviewTransform(
                                    isPreviewingBack = derivedStateOf {
                                        appState.isPreviewingBack
                                    },
                                    navigationStateBackTransform = StackNav::pop,
                                ),
                                threePanedMovableSharedElementTransform(
                                    movableSharedElementHostState = movableSharedElementHostState
                                ),
                                paneModifierTransform {
                                    if (paneState.pane == ThreePane.TransientPrimary) Modifier
                                        .fillMaxSize()
                                        .backPreview(appState.backPreviewState)
                                    else Modifier
                                        .fillMaxSize()
                                }
                            )
                        }
                    ),
                ) {
                    appState.displayScope = this
                    appState.splitLayoutState.visibleCount = appState.filteredPaneOrder.size
                    SplitLayout(
                        state = appState.splitLayoutState,
                        modifier = Modifier
                            .fillMaxSize(),
                        itemSeparators = { paneIndex, offset ->
                            PaneSeparator(
                                splitLayoutState = appState.splitLayoutState,
                                interactionSource = appState.paneInteractionSourceAt(paneIndex),
                                index = paneIndex,
                                density = density,
                                xOffset = offset,
                            )
                        },
                        itemContent = { index ->
                            val pane = appState.filteredPaneOrder[index]
                            Destination(pane)
                            if (pane == ThreePane.Primary) Destination(ThreePane.TransientPrimary)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaneSeparator(
    splitLayoutState: SplitLayoutState,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    index: Int,
    density: Density,
    xOffset: Dp,
) {
    var alpha by remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState {
        splitLayoutState.dragBy(
            index = index,
            delta = with(density) { it.toDp() }
        )
    }
    val active = interactionSource.isActive()
    Box(
        modifier = modifier
            .alpha(alpha)
            .offset(x = xOffset - (PaneSeparatorTouchTargetWidthDp / 2))
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                interactionSource = interactionSource,
            )
            .hoverable(interactionSource)
            .width(PaneSeparatorTouchTargetWidthDp)
            .fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = animateColorAsState(
                        if (active) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    ).value,
                    shape = RoundedCornerShape(PaneSeparatorActiveWidthDp),
                )
                .width(animateDpAsState(if (active) PaneSeparatorActiveWidthDp else 1.dp).value)
                .height(PaneSeparatorActiveWidthDp)
        )
    }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1000),
            block = { value, _ -> alpha = value }
        )
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

    internal var displayScope by mutableStateOf<MultiPaneDisplayScope<ThreePane, Screen>?>(
        null
    )

    val filteredPaneOrder: List<ThreePane> by derivedStateOf {
        paneRenderOrder.filter { displayScope?.destinationIn(it) != null }
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
        fun AppState.rememberMultiPaneDisplayState(
            transforms: List<Transform<ThreePane, StackNav, Screen>>,
        ): MultiPaneDisplayState<ThreePane, StackNav, Screen> {
            val displayState = remember {
                MultiPaneDisplayState(
                    panes = ThreePane.entries.toList(),
                    navigationState = navigationState,
                    backStackTransform = { stackNav ->
                        stackNav.backStack(
                            includeCurrentDestinationChildren = true,
                            placeChildrenBeforeParent = true,
                        )
                            .filterIsInstance<Screen>()
                    },
                    destinationTransform = {
                        it.current()!!
                    },
                    entryProvider = {
                        threePaneEntry(
                            paneMapping = { destination ->
                                mapOf(
                                    ThreePane.Primary to destination,
                                    ThreePane.Secondary to Screen.Demos.takeUnless(destination::equals),
                                )
                            },
                            render = { destination ->
                                destination.demoUI(
                                    destination,
                                    { navigationState.value = navigationState.value.push(it) },
                                    { navigationState.value = navigationState.value.pop() },
                                )
                            },
                        )
                    },
                    transforms = transforms,
                )
            }
            return displayState
        }
    }
}

internal val LocalAppState = staticCompositionLocalOf<AppState> {
    TODO()
}

private val PaneSeparatorActiveWidthDp = 56.dp
private val PaneSeparatorTouchTargetWidthDp = 16.dp
internal val SecondaryPaneMinWidthBreakpointDp = 600.dp
internal val TertiaryPaneMinWidthBreakpointDp = 1200.dp
