plugins {
    alias(libs.plugins.android.library)  // Android-библиотека, а не JVM!
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.distributed_messenger.presenter"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
    kotlinOptions {
        jvmTarget = "11"
    }

    // Для использования ViewModel и других компонентов Lifecycle
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":logger"))
    implementation(project(":core"))
    implementation(project(":domain"))

    // AndroidX Lifecycle и ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Тестирование
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}