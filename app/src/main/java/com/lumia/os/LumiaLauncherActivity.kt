package com.lumia.os

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
        setContent {
            MaterialTheme(colorScheme = dynamicDarkColorScheme(this)) {
                LumiaHome()
            }
        }
    }
}

@Composable
fun LumiaHome() {
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

    val filtered = apps.filter { it.loadLabel(context.packageManager).toString().contains(query, true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lumia", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Rechercher...") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
            items(filtered) { info ->
                val pm = context.packageManager
                val icon = IconCache.get(pm, info.activityInfo.packageName) { info.loadIcon(pm) }
                Column(Modifier.padding(8.dp).clickable {
                    val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                    context.startActivity(launch)
                }) {
                    Image(painter = rememberDrawablePainter(icon), contentDescription = null, modifier = Modifier.size(56.dp))
                    Text(info.loadLabel(pm).toString(), maxLines = 1, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
