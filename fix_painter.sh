#!/data/data/com.termux/files/usr/bin/bash
cd Lumia
git pull origin main

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
        window.setWindowAnimations(0)
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
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val list = context.packageManager.queryIntentActivities(intent, 0)
                .sortedBy { it.loadLabel(context.packageManager).toString().lowercase() }
            apps = list
        }
    }

    val filtered = remember(apps, query) {
        if (query.isBlank()) apps else apps.filter {
            it.loadLabel(context.packageManager).toString().contains(query, true)
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(12.dp)) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Lumia Search") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(filtered, key = { it.activityInfo.packageName }) { info ->
                val pm = context.packageManager
                // FIX: plus de rememberDrawablePainter, on convertit en bitmap direct
                val drawable = remember(info.activityInfo.packageName) {
                    IconCache.get(info.activityInfo.packageName) { info.loadIcon(pm) }
                }
                val bitmap = remember(drawable) { drawable.toBitmap(96, 96).asImageBitmap() }

                Column(Modifier.padding(6.dp).combinedClickable(onClick = {
                    val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                    context.startActivity(launch)
                }, onLongClick = {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = android.net.Uri.parse("package:${info.activityInfo.packageName}")
                    context.startActivity(intent)
                })) {
                    Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text(info.loadLabel(pm).toString(), maxLines = 1, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
EOF

git add app/src/main/java/com/lumia/os/LumiaLauncherActivity.kt
git commit -m "Fix: remove rememberDrawablePainter (accompanist removed), use toBitmap().asImageBitmap()"
git push origin main
echo "✅ FIX PAINTER PUSHÉ"
