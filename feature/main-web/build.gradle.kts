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
            implementation(projects.shared.common)
            implementation(projects.shared.interactorWeb)
            implementation(projects.shared.uiWeb)
            implementation(projects.feature.detailsWeb)
            implementation(projects.feature.feedWeb)
            implementation(projects.feature.settingsWeb)
            implementation(libs.bundles.koin.common)
            implementation(libs.bundles.jetbrains.compose.foundation.common)
            implementation(libs.bundles.jetbrains.compose.material.icons.common)
            implementation(libs.bundles.jetbrains.compose.material3.common)
            implementation(libs.bundles.jetbrains.compose.runtime.common)
        }
    }
}
