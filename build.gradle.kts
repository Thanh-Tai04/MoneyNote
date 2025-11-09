// Tệp: build.gradle.kts (ở thư mục gốc)
plugins {
    // Nâng cấp AGP
    id("com.android.application") version "8.5.0" apply false
    // Nâng cấp Kotlin
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    // Nâng cấp KSP
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
    // Nâng cấp Compose
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}