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
            implementation(projects.sharedWeb.interactorWeb)
            implementation(projects.sharedWeb.uiWeb)
            implementation(projects.featureWeb.detailsWeb)
            implementation(projects.featureWeb.feedWeb)
            implementation(projects.featureWeb.settingsWeb)
            implementation(libs.bundles.koin.common)
            implementation(libs.bundles.jetbrains.compose.foundation.common)
            implementation(libs.bundles.jetbrains.compose.material.icons.common)
            implementation(libs.bundles.jetbrains.compose.material3.common)
            implementation(libs.bundles.jetbrains.compose.runtime.common)
        }
    }
}
