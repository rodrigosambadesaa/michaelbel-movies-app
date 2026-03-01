@file:Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://developer.huawei.com/repo/") {
            content {
                includeGroupByRegex("com\\.huawei(\\..+)?")
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.huawei.agconnect") {
                useModule("com.huawei.agconnect:agcp:${requested.version}")
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven(url = "https://developer.huawei.com/repo/")
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "movies"
include(
    ":androidApp",
    ":desktopApp",
    ":iosApp",
    ":iosAppCompose",
    ":webApp",
    ":benchmark",

    ":core:platform-services:gms",
    ":core:platform-services:hms",
    ":core:platform-services:foss",
    ":core:platform-services:inject-android",
    ":core:platform-services:inject-jvm",
    ":core:platform-services:inject-ios",
    ":core:platform-services:inject-web",
    ":core:platform-services:interactor",

    ":core:analytics",
    ":core:common",
    ":core:interactor",
    ":core:network",
    ":core:persistence",
    ":core:repository",
    ":core:ui",
    ":core:widget",
    ":core:work",

    ":feature:account",
    ":feature:auth",
    ":feature:debug",
    ":feature:details",
    ":feature:fave",
    ":feature:feed",
    ":feature:gallery",
    ":feature:main",
    ":feature:notify",
    ":feature:settings"
)
