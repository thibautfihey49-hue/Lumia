plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.lumia.os"; compileSdk = 34
    defaultConfig { applicationId = "com.lumia.os"; minSdk = 26; targetSdk = 34; versionCode = 3; versionName = "3.0-ultra-pack" }
    buildTypes { release { isMinifyEnabled = true; isShrinkResources = true } }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("dev.rikka.shizuku:api:13.1.0")
    implementation("dev.rikka.shizuku:provider:13.1.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
}
