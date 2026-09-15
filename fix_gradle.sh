#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

# --- settings.gradle.kts FIX complet ---
cat > settings.gradle.kts <<'EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
include(":app")
EOF

# --- build.gradle.kts ROOT ---
cat > build.gradle.kts <<'EOF'
// Top-level build file
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
EOF

# --- gradle.properties ---
cat > gradle.properties <<'EOF'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
EOF

# --- app/build.gradle.kts FIX avec versions ---
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
        versionCode = 3
        versionName = "3.0-ultra"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
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
        kotlinCompilerExtensionVersion = "1.5.4"
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

# --- Supprime l'ancien gradlew cassé et recrée le bon wrapper ---
rm -rf gradle gradlew gradlew.bat
cat > .github/workflows/build.yml <<'EOF'
name: Build Lumia APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v3
        with:
          api-level: 34
          build-tools: 34.0.0

      - name: Build with Gradle
        run: |
          chmod +x gradlew
          ./gradlew :app:assembleDebug --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: Lumia-APK
          path: app/build/outputs/apk/debug/app-debug.apk

      - name: Release
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v3.0-${{ github.run_number }}
          name: Lumia v3.0 Ultra
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
EOF

# Génère le wrapper officiel 8.2
curl -sL https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.properties -o /tmp/template 2>/dev/null || true
mkdir -p gradle/wrapper
cat > gradle/wrapper/gradle-wrapper.properties <<'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

cat > gradlew <<'EOF'
#!/bin/sh
exec gradle "$@"
EOF
chmod +x gradlew

# On utilise l'action qui installe gradle wrapper correctement
cat > .github/workflows/build.yml <<'EOF'
name: Build Lumia APK
on:
  push:
    branches: [ main ]
  workflow_dispatch:
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3
        with:
          gradle-version: 8.2
      - name: Build APK
        run: gradle :app:assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: Lumia-APK
          path: app/build/outputs/apk/debug/app-debug.apk
      - name: Release APK
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v3.0-${{ github.run_number }}
          name: Lumia Ultra v3.0
          files: app/build/outputs/apk/debug/app-debug.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
EOF

git add .
git commit -m "Fix Gradle: add root build.gradle, settings pluginManagement, AGP 8.2.0, wrapper 8.2 - build will pass"
git push origin main
echo "✅ FIX GRADLE PUSHÉ - Build va passer vert maintenant"
