plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // Room veritabanı derleyicisi için zorunlu
}

android {
    namespace = "com.onyxera.fs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.onyxera.fs"
        minSdk = 26 // Android 8.0 - Scoped Storage ve modern UI için minimum
        targetSdk = 34
        versionCode = 3
        versionName = "2.0.0" // Solnod Maritime Edition

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Çekirdek Android ve Yaşam Döngüsü (Lifecycle)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Jetpack Compose & Material 3 Mimarisi
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Solnod Kurumsal İkon Paketi (PermMedia, DirectionsBoat, Mic vb. ekstra ikonlar için zorunlu)
    implementation("androidx.compose.material:material-icons-extended")

    // Room Veritabanı (Yerel Depolama)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Coil (Asenkron Görsel Yükleyici - Thumbnail ve Image Slider için)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Kotlin Coroutines (Arka plan asenkron veri işlemleri)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // Test Kütüphaneleri
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}