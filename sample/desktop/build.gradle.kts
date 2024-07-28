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

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
    id("kotlin-jvm-convention")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(project(":sample:common"))

                implementation(compose.desktop.currentOs)

                implementation(libs.jetbrains.compose.material)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.tunjid.mutator.core.jvm)
                implementation(libs.tunjid.mutator.coroutines.jvm)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.tunjid.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Tiling"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "Compose Examples"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "C2F20D8A-F643-4BB8-9ADD-28797B7514AF"
            }
        }
    }
}
