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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LumiaControlActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { ControlScreen() } }
    }
}

@Composable
fun ControlScreen() {
    var logs by remember { mutableStateOf("Prêt - Mode sans-root\n") }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Lumia Control - Debloat HyperOS", style = MaterialTheme.typography.headlineSmall)
            Text("0 wakelock, 0 batterie. Utilise pm disable-user --user 0\n", style = MaterialTheme.typography.labelSmall)
            Text(logs, modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = {
                // En sans-root pur on ne peut pas exec pm direct depuis une app normale
                // On génère le script ADB à coller dans Termux/PC
                logs = buildString {
                    append("Copie ce script dans Termux ou ADB PC:\n\n")
                    BloatList.all.filter { it.safe }.forEach {
                        append("pm disable-user --user 0 ${it.pkg}\n")
                    }
                    append("\nPour restaurer:\npm enable --user 0 com.miui.msa\n")
                }
            }) { Text("Générer script ADB ultra-low") }
            Spacer(Modifier.height(16.dp))
        }
        items(BloatList.all) { app ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("${app.name} - ${app.pkg}", style = MaterialTheme.typography.titleSmall)
                    Text(app.desc, style = MaterialTheme.typography.bodySmall)
                    Text(if(app.safe) "SAFE ✅" else "RISQUÉ ⚠️ Désactive seulement si tu sais", color = if(app.safe) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red)
                }
            }
        }
    }
}

// Execution réelle si Shizuku ou root (optionnel)
// Pour Termux sans root: le script généré utilise directement pm car Termux a le shell
fun execDisable(pkg: String): String {
    return try {
        val p = Runtime.getRuntime().exec(arrayOf("pm", "disable-user", "--user", "0", pkg))
        p.waitFor()
        p.inputStream.bufferedReader().readText()
    } catch (e: Exception) { e.message?: "Besoin ADB" }
}
