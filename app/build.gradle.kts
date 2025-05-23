plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.diplom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.diplom"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    implementation(libs.room.runtime.android)
    annotationProcessor(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)

    // Gson для работы с JSON
    implementation(libs.gson)

    // MPAndroidChart
    implementation(libs.mpandroidchart)
    implementation(libs.preference)
    implementation(libs.swiperefreshlayout)

    // ZXing для сканирования QR-кодов
    implementation(libs.zxing.android.embedded)
    implementation(libs.zxing.core)
}