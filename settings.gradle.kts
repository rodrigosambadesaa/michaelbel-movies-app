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
    ":core:notifications",
    ":core:persistence",
    ":core:repository",
    ":core:ui",
    ":core:widget",
    ":core:work",

    ":feature:main",
    ":feature:account",
    ":feature:auth",
    ":feature:details",
    ":feature:feed",
    ":feature:gallery",
    ":feature:search",
    ":feature:settings",

    ":feature:debug",
    ":feature:debug-impl",

    ":core:ui-web",
    ":feature:main-impl-web",
    ":feature:feed-web",
    ":feature:feed-impl-web"
)
