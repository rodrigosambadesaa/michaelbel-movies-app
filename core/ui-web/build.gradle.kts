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
            api(libs.bundles.jetbrains.compose.animation.common)
            api(libs.bundles.jetbrains.compose.components.common)
            api(libs.bundles.jetbrains.compose.foundation.common)
            api(libs.bundles.jetbrains.compose.material.common)
            api(libs.bundles.jetbrains.compose.material3.common)
            api(libs.bundles.jetbrains.compose.runtime.common)
            api(libs.bundles.jetbrains.compose.runtime.saveable.common)
            api(libs.bundles.jetbrains.compose.ui.common)
            api(libs.bundles.jetbrains.compose.ui.tooling.common)
            implementation(libs.bundles.jetbrains.compose.material.icons.common)
        }
    }

    compilerOptions {
        jvmToolchain(libs.versions.jdk.get().toInt())
    }
}
