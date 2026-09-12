// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android app plugin
    id("com.android.application") version "9.3.2" apply false
    // Android library plugin
    id("com.android.library") version "9.3.2" apply false
    // Kotlin JVM
    id("org.jetbrains.kotlin.jvm") version "2.4.10" apply false
    // Jetpack Compose support
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false
    // Typed Nav3 keys
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10" apply false
    // Kotlin Symbol Processing
    id("com.google.devtools.ksp") version "2.3.11" apply false
    // Optional Parcelable support
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.4.10" apply false
    // Hilt Dependency Injection
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    alias(libs.plugins.android.test) apply false
}