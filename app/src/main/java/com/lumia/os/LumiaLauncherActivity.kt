package com.lumia.os

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LumiaLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LumiaFluidTheme { LumiaFluidHome() } }
    }
}

@Composable
fun LumiaFluidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF000000),
            surface = Color(0xFF0A0A0A),
            primary = Color(0xFF00FF88),
            onBackground = Color.White
        ),
        content = content
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LumiaFluidHome() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<ResolveInfo>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val list = context.packageManager.queryIntentActivities(intent, 0)
                .sortedWith(compareBy({ !it.activityInfo.packageName.contains("lumia") }, { it.loadLabel(context.packageManager).toString().lowercase() }))
            apps = list
        }
    }
    val filtered by remember(apps, query) { derivedStateOf { if (query.isBlank()) apps else apps.filter { try { it.loadLabel(context.packageManager).toString().contains(query, true) } catch(_: Exception) { false } } } }
    val dockApps = remember(filtered) { filtered.take(4) }
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Surface(Modifier.fillMaxWidth().padding(16.dp), RoundedCornerShape(24.dp), color = Color(0xFF1A1A1A)) {
                OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Rechercher...", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White))
            }
            Text("${filtered.size} apps • Lumia 4.0 Fluid", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.weight(1f).padding(horizontal = 8.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                items(filtered, key = { it.activityInfo.packageName + it.activityInfo.name }) { info -> FluidAppIcon(info) }
            }
        }
        if (query.isBlank() && dockApps.isNotEmpty()) {
            Surface(Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(), RoundedCornerShape(28.dp), color = Color(0xCC1A1A1A), tonalElevation = 8.dp) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) { dockApps.forEach { FluidAppIcon(it, true) } }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FluidAppIcon(info: ResolveInfo, isDock: Boolean = false) {
    val context = LocalContext.current
    val pm = context.packageManager
    var bitmap by remember(info.activityInfo.packageName) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val scale by animateFloatAsState(1f, tween(100), label = "scale")
    LaunchedEffect(info.activityInfo.packageName) {
        withContext(Dispatchers.IO) {
            try {
                val drawable = IconCache.get(info.activityInfo.packageName) { info.loadIcon(pm) }
                bitmap = drawable.toBitmap(96, 96).asImageBitmap()
            } catch(_: Exception) {}
        }
    }
    Column(Modifier.padding(if(isDock) 4.dp else 8.dp).scale(scale).clip(RoundedCornerShape(16.dp)).combinedClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = { try { pm.getLaunchIntentForPackage(info.activityInfo.packageName)?.let { context.startActivity(it) } } catch(_: Exception) {} }, onLongClick = { try { val i = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS); i.data = android.net.Uri.parse("package:${info.activityInfo.packageName}"); context.startActivity(i) } catch(_: Exception) {} }), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(if (isDock) 52.dp else 56.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF1A1A1A)), Alignment.Center) {
            if (bitmap != null) Image(bitmap!!, null, Modifier.size(40.dp)) else Box(Modifier.size(40.dp))
        }
        if (!isDock) { Spacer(Modifier.height(4.dp)); Text(try { info.loadLabel(pm).toString() } catch(_: Exception) { "App" }, color = Color.White, fontSize = 10.sp, maxLines = 1, textAlign = TextAlign.Center, modifier = Modifier.width(64.dp)) }
    }
}
