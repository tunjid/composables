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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.tunjid.composables.backpreview.BackPreviewState
import com.tunjid.composables.splitlayout.SplitLayout
import com.tunjid.composables.splitlayout.SplitLayoutState
import com.tunjid.demo.common.app.demos.utilities.isActive
import com.tunjid.demo.common.ui.AppState.Companion.rememberMultiPaneDisplayState
import com.tunjid.treenav.StackNav
import com.tunjid.treenav.compose.MultiPaneDisplay
import com.tunjid.treenav.compose.MultiPaneDisplayState
import com.tunjid.treenav.compose.PaneNavigationState
import com.tunjid.treenav.compose.moveablesharedelement.MovableSharedElementHostState
import com.tunjid.treenav.compose.multiPaneDisplayBackstack
import com.tunjid.treenav.compose.panedecorators.PaneDecorator
import com.tunjid.treenav.compose.threepane.ThreePane
import com.tunjid.treenav.compose.threepane.panedecorators.threePaneAdaptiveDecorator
import com.tunjid.treenav.compose.threepane.panedecorators.threePaneMovableSharedElementDecorator
import com.tunjid.treenav.compose.threepane.threePaneEntry
import com.tunjid.treenav.pop
import com.tunjid.treenav.push
import com.tunjid.treenav.requireCurrent

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
                        sharedTransitionScope = this,
                    )
                }
                val windowWidth = rememberUpdatedState(
                    with(density) {
                        LocalWindowInfo.current.containerSize.width.toDp()
                    },
                )
                MultiPaneDisplay(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = appState.rememberMultiPaneDisplayState(
                        remember {
                            listOf(
                                threePaneAdaptiveDecorator(
                                    secondaryPaneBreakPoint = mutableStateOf(
                                        SecondaryPaneMinWidthBreakpointDp,
                                    ),
                                    tertiaryPaneBreakPoint = mutableStateOf(
                                        TertiaryPaneMinWidthBreakpointDp,
                                    ),
                                    windowWidthState = windowWidth,
                                ),
                                threePaneMovableSharedElementDecorator(
                                    movableSharedElementHostState = movableSharedElementHostState,
                                ),
                            )
                        },
                    ),
                ) {
                    val splitPaneState = remember {
                        SplitPaneState(
                            paneNavigationState = this::paneNavigationState,
                        )
                    }
                    CompositionLocalProvider(
                        LocalSplitPaneState provides splitPaneState,
                    ) {
                        SplitLayout(
                            state = splitPaneState.splitLayoutState,
                            modifier = Modifier
                                .fillMaxSize(),
                            itemSeparators = { paneIndex, offset ->
                                PaneSeparator(
                                    splitLayoutState = splitPaneState.splitLayoutState,
                                    interactionSource = appState.paneInteractionSourceAt(paneIndex),
                                    index = paneIndex,
                                    density = density,
                                    xOffset = offset,
                                )
                            },
                            itemContent = { index ->
                                Destination(splitPaneState.filteredPaneOrder[index])
                            },
                        )
                    }
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
            delta = with(density) { it.toDp() },
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
            .fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = animateColorAsState(
                        if (active) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                    ).value,
                    shape = RoundedCornerShape(PaneSeparatorActiveWidthDp),
                )
                .width(animateDpAsState(if (active) PaneSeparatorActiveWidthDp else 1.dp).value)
                .height(PaneSeparatorActiveWidthDp),
        )
    }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1000),
            block = { value, _ -> alpha = value },
        )
    }
}

@Stable
class AppState {
    private val navigationState = mutableStateOf(
        StackNav(
            name = "demo app",
            children = listOf(Screen.Demos),
        ),
    )

    private val paneInteractionSourceList = mutableStateListOf<MutableInteractionSource>()

    val backPreviewState = BackPreviewState()

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
            decorators: List<PaneDecorator<StackNav, Screen, ThreePane>>,
        ): MultiPaneDisplayState<StackNav, Screen, ThreePane> {
            val saveableStateHolderNavEntryDecorator =
                rememberSaveableStateHolderNavEntryDecorator<Screen>()
            val displayState = remember {
                MultiPaneDisplayState(
                    panes = ThreePane.entries.toList(),
                    navigationState = navigationState,
                    backStackTransform = StackNav::multiPaneDisplayBackstack,
                    destinationTransform = StackNav::requireCurrent,
                    popTransform = StackNav::pop,
                    onPopped = navigationState::value::set,
                    paneDecorators = decorators,
                    navEntryDecorators = listOf(
                        saveableStateHolderNavEntryDecorator,
                    ),
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
                )
            }
            return displayState
        }
    }
}

@Stable
internal class SplitPaneState(
    paneNavigationState: () -> PaneNavigationState<ThreePane, Screen>,
) {

    internal val filteredPaneOrder by derivedStateOf {
        PaneRenderOrder.filter { paneNavigationState().destinationIn(it) != null }
    }

    internal val splitLayoutState = SplitLayoutState(
        orientation = Orientation.Horizontal,
        maxCount = 2,
        minSize = 10.dp,
        visibleCount = {
            filteredPaneOrder.size
        },
        keyAtIndex = { index ->
            filteredPaneOrder[index]
        },
    )
}

internal val LocalSplitPaneState = staticCompositionLocalOf<SplitPaneState> {
    throw IllegalStateException("LocalSplitPaneState not set")
}

internal val LocalAppState = staticCompositionLocalOf<AppState> {
    throw IllegalStateException("LocalAppState not set")
}

private val PaneRenderOrder = listOf(
    ThreePane.Tertiary,
    ThreePane.Secondary,
    ThreePane.Primary,
)

private val PaneSeparatorActiveWidthDp = 56.dp
private val PaneSeparatorTouchTargetWidthDp = 16.dp
internal val SecondaryPaneMinWidthBreakpointDp = 600.dp
internal val TertiaryPaneMinWidthBreakpointDp = 1200.dp
