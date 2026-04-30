-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class org.slf4j.** { *; }

-dontnote io.ktor.**
-dontnote kotlinx.coroutines.**
-dontnote kotlinx.serialization.**
-dontnote org.slf4j.**

-dontwarn io.ktor.**
-dontwarn io.ktor.client.HttpClientEngineContainer
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.serialization.**
