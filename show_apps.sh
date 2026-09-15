#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

# --- Ajoute LAUNCHER à toutes les apps dans AndroidManifest.xml ---
cat > app/src/main/AndroidManifest.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:ignore="QueryAllPackagesPermission" />
    <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" tools:ignore="ProtectedPermissions" />

    <application
        android:name=".LumiaApp"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="Lumia"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">

        <!-- LAUNCHER PRINCIPAL -->
        <activity android:name=".LumiaLauncherActivity" android:exported="true" android:launchMode="singleTask" android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>

        <activity android:name=".control.LumiaControlActivity" android:exported="true" android:label="Lumia Control">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <activity android:name=".gallery.GalleryActivity" android:exported="true" android:label="Lumia Gallery">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <activity android:name=".files.FilesActivity" android:exported="true" android:label="Lumia Files">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <activity android:name=".guard.GuardActivity" android:exported="true" android:label="Lumia Guard">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <activity android:name=".lock.LockActivity" android:exported="true" android:label="Lumia Lock">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <activity android:name=".feed.FeedActivity" android:exported="true" android:label="Lumia Feed">
            <intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter>
        </activity>

        <service android:name=".keyboard.LumiaKeyboard" android:label="Lumia Keyboard" android:permission="android.permission.BIND_INPUT_METHOD" android:exported="true">
            <intent-filter><action android:name="android.view.InputMethod" /></intent-filter>
            <meta-data android:name="android.view.im" android:resource="@xml/method" />
        </service>

    </application>
</manifest>
EOF

# --- Update Launcher pour mettre Lumia en haut ---
cat > app/src/main/java/com/lumia/os/LumiaLauncherActivity.kt <<'EOF'
package com.lumia.os

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LumiaLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try { overridePendingTransition(0,0) } catch(_: Exception) {}
        setContent { MaterialTheme { LumiaHomeUltra() } }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LumiaHomeUltra() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<ResolveInfo>>(emptyList()) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                val list = context.packageManager.queryIntentActivities(intent, 0)
                    .sortedWith(compareBy({ !it.activityInfo.packageName.contains("lumia") }, { it.loadLabel(context.packageManager).toString().lowercase() }))
                apps = list
            } catch(e: Exception) { apps = emptyList() }
        }
    }

    val filtered = remember(apps, query) {
        if (query.isBlank()) apps else apps.filter {
            try { it.loadLabel(context.packageManager).toString().contains(query, true) } catch(_: Exception) { false }
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(12.dp)) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Lumia Search - tape 'Lumia'") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        Text("Lumia 3.1 - ${filtered.size} apps - Tape 'Lumia' pour voir les 7 apps", style = MaterialTheme.typography.labelSmall)

        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(filtered, key = { it.activityInfo.packageName + it.activityInfo.name }) { info ->
                val pm = context.packageManager
                var iconBitmap by remember(info.activityInfo.packageName) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
                LaunchedEffect(info.activityInfo.packageName) {
                    withContext(Dispatchers.IO) {
                        try {
                            val drawable = IconCache.get(info.activityInfo.packageName) { info.loadIcon(pm) }
                            iconBitmap = drawable.toBitmap(96, 96).asImageBitmap()
                        } catch(_: Exception) {}
                    }
                }
                Column(modifier = Modifier.padding(6.dp).fillMaxWidth().combinedClickable(
                    onClick = { try { pm.getLaunchIntentForPackage(info.activityInfo.packageName)?.let { context.startActivity(it) } } catch(_: Exception) {} },
                    onLongClick = { try { val i = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS); i.data = android.net.Uri.parse("package:${info.activityInfo.packageName}"); context.startActivity(i) } catch(_: Exception) {} }
                )) {
                    if (iconBitmap != null) Image(bitmap = iconBitmap!!, contentDescription = null, modifier = Modifier.size(48.dp)) else Box(Modifier.size(48.dp))
                    Text(try { info.loadLabel(pm).toString() } catch(_: Exception) { "App" }, maxLines = 1, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
EOF

git add .
git commit -m "Make all Lumia apps visible in launcher: add LAUNCHER intent + Lumia apps on top"
git push origin main
echo "✅ APPS VISIBLES PUSHÉ"
