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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LumiaLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // LOW RAM: on coupe les animations système
        window.setWindowAnimations(0)
        setContent {
            MaterialTheme { LumiaHomeUltra() }
        }
    }
}

@Composable
fun LumiaHomeUltra() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<ResolveInfo>>(emptyList()) }
    var query by remember { mutableStateOf("") }

    // LOW BATTERIE: 1 seul load au démarrage, jamais en background
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
        // Pas de TextField Material lourd, juste un champ natif
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Lumia Search") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))

        // LOW RAM: LazyVerticalGrid avec recyclage + key = pas de recomposition
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(filtered, key = { it.activityInfo.packageName }) { info ->
                val pm = context.packageManager
                // Icon cache ultra petit
                val drawable = remember(info.activityInfo.packageName) {
                    IconCache.get(info.activityInfo.packageName) { info.loadIcon(pm) }
                }
                Column(Modifier.padding(6.dp).combinedClickable(onClick = {
                    val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                    context.startActivity(launch)
                }, onLongClick = {
                    // Long press = info app sans service background
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = android.net.Uri.parse("package:${info.activityInfo.packageName}")
                    context.startActivity(intent)
                })) {
                    Image(painter = rememberDrawablePainter(drawable), contentDescription = null, modifier = Modifier.size(48.dp))
                    Text(info.loadLabel(pm).toString(), maxLines = 1, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
