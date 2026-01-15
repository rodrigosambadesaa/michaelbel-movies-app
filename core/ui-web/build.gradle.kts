@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    js { browser {} }
    wasmJs { browser {} }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.bundles.coil.common)
            api(libs.bundles.jetbrains.androidx.navigation3.common)
            api(libs.bundles.jetbrains.androidx.core.common)
            api(libs.jetbrains.compose.animation)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.runtime.saveable)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.material)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.compose.material.icons.extended)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
