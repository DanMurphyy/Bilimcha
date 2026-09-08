plugins {
// Required for Android app
    id("com.android.application")

// Compose support (compiler + runtime)
    id("org.jetbrains.kotlin.plugin.compose")

// Serialization support for typed Navigation 3 keys
    id("org.jetbrains.kotlin.plugin.serialization")

// Parcelize support
    id("org.jetbrains.kotlin.plugin.parcelize")

// Optional: Dependency Injection (Hilt)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")// required for Hilt codegen
}

android {
    namespace = "com.danmurphyy.bilimcha"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.danmurphyy.bilimcha"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android / Kotlin
    // Kotlin extensions for Android
    implementation("androidx.core:core-ktx:1.17.0")
    // Lifecycle runtime, required for Compose + Nav3
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    // Compose integration with Activity
    implementation("androidx.activity:activity-compose:1.12.2")

// Compose UI
    // BOM ensures consistent Compose versions
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    // Core Compose UI
    implementation("androidx.compose.ui:ui")
    // Graphics & drawing
    implementation("androidx.compose.ui:ui-graphics")
    // Preview support in IDE
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Material3 components
    implementation("androidx.compose.material3:material3:1.4.0-alpha05")

    implementation("androidx.compose.material:material-icons-extended:1.7.0")

// Debug / tooling
    // UI inspection / preview tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Compose testing manifests
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Navigation 3
    // Nav3 back stack & state
    implementation("androidx.navigation3:navigation3-runtime:1.0.0-alpha01")
    // Compose integration
    implementation("androidx.navigation3:navigation3-ui:1.0.0-alpha01")
    // Back stack survives rotation / config changes
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
    // Serialization (Mandatory for Nav3 typed keys)
    // Required for typed NavKeys (Serializable / Parcelable)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.60.1")
    // Hilt codegen
    ksp("com.google.dagger:hilt-compiler:2.60.1")
    // Required for hiltViewModel() in Jetpack Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Testing
    // Unit tests
    testImplementation("junit:junit:4.13.2")
    // AndroidX JUnit tests
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    // Espresso UI testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    // BOM for Compose UI tests
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    // Compose UI testing framework
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}