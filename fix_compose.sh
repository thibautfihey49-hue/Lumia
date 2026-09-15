#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

# --- FIX: Compose 1.5.8 compatible avec Kotlin 1.9.22 ---
cat > app/build.gradle.kts <<'EOF'
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lumia.os"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lumia.os"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "3.1-ultra"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("dev.rikka.shizuku:api:13.1.0")
    implementation("dev.rikka.shizuku:provider:13.1.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
}
EOF

git add app/build.gradle.kts
git commit -m "Fix Compose compiler 1.5.8 for Kotlin 1.9.22"
git push origin main
echo "✅ COMPOSE FIX PUSHÉ"
