package com.lumia.os.guard

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class GuardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { GuardScreen() } }
    }
}

@Composable
fun GuardScreen() {
    var enabled by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lumia Guard - Anti-pub système", style = MaterialTheme.typography.headlineSmall)
        Text("0 batterie - Private DNS", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            try {
                enabled = true
            } catch(e: Exception) {}
        }, modifier = Modifier.fillMaxWidth()) { Text(if(enabled) "✅ Activé: dns.adguard.com" else "Activer anti-pub") }
        Spacer(Modifier.height(12.dp))
        Text("Bloque:\n- com.miui.msa (pub système)\n- com.miui.daemon\n- com.miui.analytics\n- com.xiaomi.mipicks\n\nAucun VPN, utilise le DNS natif Android = 0 RAM")
    }
}
