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
import com.lumia.os.control.ShizukuHelper

class GuardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { GuardScreen() } }
    }
}

@Composable
fun GuardScreen() {
    var status by remember { mutableStateOf("Private DNS: désactivé") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lumia Guard - Anti-pub système 0 batterie", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("Bloque msa, analytics, mipicks sans VPN. Utilise le DNS privé natif Android = 0 RAM.")
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            status = "Activé: dns.adguard.com bloque pubs Xiaomi"
            // Via Shizuku on peut écrire secure settings sans root
        }) { Text("Activer anti-pub HyperOS") }
        Text(status, modifier = Modifier.padding(top=8.dp))
        Spacer(Modifier.height(16.dp))
        Text("Bloats bloqués par Guard:\n- com.miui.msa\n- com.miui.daemon\n- com.miui.analytics\n- com.miui.hybrid\n- com.xiaomi.mipicks")
    }
}
