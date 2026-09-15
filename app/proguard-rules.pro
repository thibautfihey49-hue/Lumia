-keep class com.lumia.os.** { *; }
-optimizations !code/allocation/variable
# Garde le launcher ultra rapide au démarrage
-keepclassmembers class * extends androidx.compose.runtime.Composer { *; }
