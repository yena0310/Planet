plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp) // ✅ KSP 적용
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.planet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.planet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (버전 정합성 보장)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.firebase.crashlytics.buildtools)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Compose Material & Material3
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material:1.6.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // ConstraintLayout for Compose
    implementation(libs.androidx.constraintlayout)

    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))    // Firebase BoM
    implementation("com.google.firebase:firebase-auth")    // Firebase Auth
    implementation("com.google.firebase:firebase-database")    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-firestore")    // Firebase Firestore

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries

    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("com.google.guava:guava:32.1.2-android")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // implementation("androidx.compose.material:material:1.6.1") 위에 있음


}