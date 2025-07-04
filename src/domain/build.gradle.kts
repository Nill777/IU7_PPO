plugins {
    alias(libs.plugins.android.library)  // Android-библиотека
    alias(libs.plugins.kotlin.android)    // Kotlin для Android (включает JVM-функциональность)
}

android {
    namespace = "com.distributed_messenger.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    // Для тестов с корутинами
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
            }
        }
    }
}

dependencies {
    implementation(project(":logger"))
    implementation(project(":core"))

    // Coroutines для suspend-функций
    implementation(libs.kotlinx.coroutines.core)

    // Unit тесты
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
}