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

package com.tunjid.demo

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.tunjid.demo.common.ui.App
import com.tunjid.demo.common.ui.AppTheme

fun main() {
    application {
        val windowState = rememberWindowState(
            size = DpSize(400.dp, 800.dp)
        )
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Tiling Demo"
        ) {
            AppTheme {
                App()
            }
        }
    }
}


