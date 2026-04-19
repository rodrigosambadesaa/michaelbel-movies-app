-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class org.slf4j.** { *; }
-keep class org.michaelbel.** { *; }

-keepclassmembers class * implements io.ktor.client.HttpClientEngineContainer { *; }

-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.serialization.**
