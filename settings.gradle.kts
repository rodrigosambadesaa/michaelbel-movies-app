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

    ":shared:platform-services:gms",
    ":shared:platform-services:hms",
    ":shared:platform-services:foss",
    ":shared:platform-services:inject-android",
    ":shared:platform-services:inject-jvm",
    ":shared:platform-services:inject-ios",
    ":shared:platform-services:inject-web",
    ":shared:platform-services:interactor",

    ":shared:analytics",
    ":shared:common",
    ":shared:interactor",
    ":shared:interactor-web",
    ":shared:network",
    ":shared:persistence",
    ":shared:persistence-web",
    ":shared:repository",
    ":shared:repository-web",
    ":shared:ui",
    ":shared:ui-web",
    ":shared:widget",
    ":shared:work",

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
