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

plugins {
    id("android-library-convention")
    id("kotlin-library-convention")
    id("org.jetbrains.compose")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":library:composables"))

                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.animation)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.foundation.layout)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.jetbrains.compose.material.icons.extended)

                api(libs.tunjid.mutator.core.common)
                api(libs.tunjid.mutator.coroutines.common)

                api(libs.tunjid.treenav)
                api(libs.tunjid.treenav.compose)
                api(libs.tunjid.treenav.compose.threepane)
            }
        }
        named("androidMain") {
            dependencies {
            }
        }
    }
}

compose.experimental {
    web.application {}
}
