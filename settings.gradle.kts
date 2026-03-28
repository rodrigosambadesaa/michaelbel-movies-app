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
        mavenLocal()
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
    ":core:interactor-web",
    ":core:network",
    ":core:persistence",
    ":core:persistence-web",
    ":core:repository",
    ":core:repository-web",
    ":core:ui",
    ":core:widget",
    ":core:work",

    ":feature:account",
    ":feature:about",
    ":feature:auth",
    ":feature:debug",
    ":feature:details",
    ":feature:details-web",
    ":feature:fave",
    ":feature:feed",
    ":feature:feed-web",
    ":feature:gallery",
    ":feature:main",
    ":feature:main-web",
    ":feature:notify",
    ":feature:settings",
    ":feature:settings-web"
)
