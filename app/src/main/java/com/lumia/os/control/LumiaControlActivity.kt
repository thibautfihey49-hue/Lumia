package com.lumia.os.control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class LumiaControlActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { ControlScreen() } }
    }
}

@Composable
fun ControlScreen() {
    val bloats = remember { listOf(
        "com.miui.msa" to "MSA Pub",
        "com.miui.daemon" to "Daemon",
        "com.miui.analytics" to "Analytics",
        "com.xiaomi.mipicks" to "Mi Picks",
        "com.miui.hybrid" to "Hybrid",
        "com.miui.yellowpage" to "Yellow Page"
    )}
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Lumia Control - Debloat 1-clic", style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.height(8.dp)); Text("Shizuku requis pour 1-clic sans PC. Sinon utilise install.sh"); Spacer(Modifier.height(12.dp)) }
        items(bloats) { (pkg, name) ->
            var disabled by remember { mutableStateOf(false) }
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp)) {
                Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text(name); Text(pkg, style = MaterialTheme.typography.labelSmall) }
                    Button(onClick = {
                        disabled = ShizukuHelper.runPmCommand(pkg, true)
                    }) { Text(if(disabled) "✅" else "Désactiver") }
                }
            }
        }
    }
}
