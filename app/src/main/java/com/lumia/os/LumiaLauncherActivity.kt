package com.lumia.os

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LumiaLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = Color.Black, surface = Color.Black, primary = Color(0xFF00FF88))) {
                UltraLauncher()
            }
        }
    }
}

@Composable
fun UltraLauncher() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var apps by remember { mutableStateOf<List<android.content.pm.ResolveInfo>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var ram by remember { mutableStateOf("...") }
    var ping by remember { mutableStateOf("-- ms") }

    fun refreshRam() {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mem = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mem)
        ram = "${mem.availMem / 1024 / 1024}MB libre"
    }

    fun boost() {
        scope.launch(Dispatchers.IO) {
            // 1. KILL ALL BACKGROUND - max perf Xiaomi
            try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                am.runningAppProcesses?.forEach {
                    if (it.importance > 120) {
                        try { am.killBackgroundProcesses(it.processName) } catch (_: Exception) {}
                    }
                }
            } catch (_: Exception) {}
            // 2. Ping test ultra rapide
            val start = System.currentTimeMillis()
            try { Runtime.getRuntime().exec("/system/bin/ping -c 1 -W 1 1.1.1.1").waitFor() } catch (_: Exception) {}
            val ms = System.currentTimeMillis() - start
            withContext(Dispatchers.Main) {
                ping = "${ms}ms"
                refreshRam()
                Toast.makeText(context, "BOOSTED: ${ms}ms ping", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshRam()
        withContext(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val list = context.packageManager.queryIntentActivities(intent, 0)
                .sortedBy { it.loadLabel(context.packageManager).toString().lowercase() }
            apps = list
        }
        // Ping auto au démarrage
        scope.launch(Dispatchers.IO) {
            val s = System.currentTimeMillis()
            try { Runtime.getRuntime().exec("/system/bin/ping -c 1 -W 1 1.1.1.1").waitFor() } catch (_: Exception) {}
            val ms = System.currentTimeMillis() - s
            withContext(Dispatchers.Main) { ping = "${ms}ms" }
        }
    }

    val filtered by remember(apps, query) {
        derivedStateOf { if (query.isBlank()) apps else apps.filter { try { it.loadLabel(context.packageManager).toString().contains(query, true) } catch (_: Exception) { false } } }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            // TOP BAR ULTRA LEGER - RAM + PING + BOOST
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(ram, color = Color(0xFF00FF88), fontSize = 11.sp)
                    Text("PING $ping", color = if (ping.replace("ms","").toIntOrNull() ?: 999 < 80) Color(0xFF00FF88) else Color.Yellow, fontSize = 11.sp)
                }
                Row {
                    Button(onClick = { boost() }, colors = ButtonDefaults.buttonColors(Color(0xFF00FF88), Color.Black), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                        Text("BOOST", fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = {
                        val pkgs = listOf("com.activision.callofduty.shooter", "com.garena.game.codm")
                        pkgs.forEach { pkg ->
                            try { context.packageManager.getLaunchIntentForPackage(pkg)?.let { context.startActivity(it); return@Button } } catch (_: Exception) {}
                        }
                        Toast.makeText(context, "COD non trouvé", Toast.LENGTH_SHORT).show()
                    }, colors = ButtonDefaults.buttonColors(Color.White, Color.Black), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                        Text("COD", fontSize = 12.sp)
                    }
                }
            }

            // SEARCH MINIMALISTE
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("App...", fontSize = 12.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF222222), unfocusedBorderColor = Color(0xFF111111), focusedContainerColor = Color(0xFF0A0A0A), unfocusedContainerColor = Color(0xFF0A0A0A), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Text("${filtered.size} apps", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(start = 12.dp, top = 4.dp))

            // GRILLE ULTRA OPTIMISÉE - 5 colonnes pour plus de FPS (moins de scroll)
            LazyVerticalGrid(columns = GridCells.Fixed(5), modifier = Modifier.weight(1f).padding(4.dp)) {
                items(filtered, key = { it.activityInfo.packageName }) { info ->
                    UltraApp(info)
                }
            }
        }
    }
}

@Composable
fun UltraApp(info: android.content.pm.ResolveInfo) {
    val context = LocalContext.current
    var bitmap by remember(info.activityInfo.packageName) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(info.activityInfo.packageName) {
        withContext(Dispatchers.IO) {
            try {
                val d = info.loadIcon(context.packageManager)
                bitmap = d.toBitmap(72, 72).asImageBitmap()
            } catch (_: Exception) {}
        }
    }
    Column(Modifier.padding(4.dp).clickable {
        try { context.packageManager.getLaunchIntentForPackage(info.activityInfo.packageName)?.let { context.startActivity(it) } } catch (_: Exception) {}
    }.width(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (bitmap != null) Image(bitmap!!, null, Modifier.size(48.dp)) else Box(Modifier.size(48.dp).background(Color(0xFF111111), RoundedCornerShape(10.dp)))
        Text(try { info.loadLabel(context.packageManager).toString() } catch (_: Exception) { "" }, color = Color.White, fontSize = 8.sp, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.width(60.dp))
    }
}
