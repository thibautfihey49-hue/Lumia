package com.lumia.os.feed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FeedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { FeedLow() } }
    }
}

@Composable
fun FeedLow() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lumia Feed - Remplace Google Discover", style = MaterialTheme.typography.headlineSmall)
        Card(Modifier.fillMaxWidth().padding(top=12.dp)) { Column(Modifier.padding(12.dp)) { Text("Météo: 18°C Angers - Local, 0 réseau"); Text("Batterie: Optimisée"); Text("Stockage libéré: 1.2GB via Control") } }
        Card(Modifier.fillMaxWidth().padding(top=8.dp)) { Column(Modifier.padding(12.dp)) { Text("Raccourcis"); Text("• Gallery Low\n• Files Low\n• Control") } }
    }
}
