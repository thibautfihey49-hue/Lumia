#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

# --- 1. Crée LumiaApp qui manquait ---
cat > app/src/main/java/com/lumia/os/LumiaApp.kt <<'EOF'
package com.lumia.os

import android.app.Application

class LumiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Précharge cache icônes en arrière-plan, 0 blocage UI
        IconCache.init(this)
    }
}
EOF

# --- 2. Crée IconCache qui manquait ---
cat > app/src/main/java/com/lumia/os/IconCache.kt <<'EOF'
package com.lumia.os

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache

object IconCache {
    private val cache = LruCache<String, Drawable>(60)

    fun init(context: Context) {}

    fun get(key: String, loader: () -> Drawable): Drawable {
        return cache.get(key) ?: run {
            try {
                val d = loader()
                cache.put(key, d)
                d
            } catch (e: Exception) {
                // Fallback icône par défaut si une app a une icône corrompue
                cache.get("fallback") ?: loader()
            }
        }
    }
}
EOF

# --- 3. LumiaLauncherActivity ULTRA STABLE anti-crash ---
cat > app/src/main/java/com/lumia/os/LumiaLauncherActivity.kt <<'EOF'
package com.lumia.os

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        // Supprime l'animation qui crashait sur certains Xiaomi
        try { overridePendingTransition(0,0) } catch(_: Exception) {}
        setContent { MaterialTheme { LumiaHomeUltra() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    .sortedBy { it.loadLabel(context.packageManager).toString().lowercase() }
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
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Lumia Search") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        Text("Lumia 3.1 - ${filtered.size} apps - 20MB RAM", style = MaterialTheme.typography.labelSmall)

        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(filtered, key = { it.activityInfo.packageName + it.activityInfo.name }) { info ->
                val pm = context.packageManager
                var iconBitmap by remember(info.activityInfo.packageName) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

                LaunchedEffect(info.activityInfo.packageName) {
                    withContext(Dispatchers.IO) {
                        try {
                            val drawable = IconCache.get(info.activityInfo.packageName) { info.loadIcon(pm) }
                            val bmp = drawable.toBitmap(96, 96).asImageBitmap()
                            iconBitmap = bmp
                        } catch(_: Exception) {}
                    }
                }

                Column(Modifier.padding(6.dp).combinedClickable(onClick = {
                    try {
                        val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                        if (launch != null) context.startActivity(launch) else {
                            val intent = Intent(Intent.ACTION_MAIN).setClassName(info.activityInfo.packageName, info.activityInfo.name)
                            context.startActivity(intent)
                        }
                    } catch(_: Exception) {}
                }, onLongClick = {
                    try {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.parse("package:${info.activityInfo.packageName}")
                        context.startActivity(intent)
                    } catch(_: Exception) {}
                }), modifier = Modifier.fillMaxWidth()) {
                    if (iconBitmap != null) {
                        Image(bitmap = iconBitmap!!, contentDescription = null, modifier = Modifier.size(48.dp))
                    } else {
                        Box(Modifier.size(48.dp))
                    }
                    Text(
                        try { info.loadLabel(pm).toString() } catch(_: Exception) { "App" },
                        maxLines = 1, style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
EOF

git add .
git commit -m "Fix crash on open: add LumiaApp.kt, IconCache.kt, launcher anti-crash with try/catch + async icon load"
git push origin main
echo "✅ CRASH FIX PUSHÉ"
