plugins {
    id("org.jetbrains.kotlin.jvm")  // Чистая Kotlin/JVM-библиотека
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
// важно для unit тестов
tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":core"))
//    implementation(project(":data"))

    // Coroutines для suspend-функций
    implementation(libs.kotlinx.coroutines.core)

    // Unit тесты
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
}