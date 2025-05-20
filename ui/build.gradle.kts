plugins {
    alias(libs.plugins.android.library)  // Android-библиотека для Compose
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)   // Обязательно для Compose
}

android {
    namespace = "com.distributed_messenger.ui"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true  // Включаем поддержку Compose
    }
}

dependencies {
    implementation(project(":logger"))
    // Зависимость на presenter (для NavigationController и AuthViewModel)
    implementation(project(":core"))
    implementation(project(":presenter"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)  // Для NavigationController

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Тестирование
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}