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
                            iconBitmap = drawable.toBitmap(96, 96).asImageBitmap()
                        } catch(_: Exception) {}
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                try {
                                    val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                                    if (launch != null) context.startActivity(launch)
                                } catch(_: Exception) {}
                            },
                            onLongClick = {
                                try {
                                    val i = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    i.data = android.net.Uri.parse("package:${info.activityInfo.packageName}")
                                    context.startActivity(i)
                                } catch(_: Exception) {}
                            }
                        )
                ) {
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
