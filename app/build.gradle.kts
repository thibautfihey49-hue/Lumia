plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.lumia.os"; compileSdk = 34
    defaultConfig {
        applicationId = "com.lumia.os"; minSdk = 26; targetSdk = 34; versionCode = 2; versionName = "2.0-ultra-low"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
}
