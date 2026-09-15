package com.lumia.os.feed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FeedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { 
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Lumia Feed", style = MaterialTheme.typography.headlineSmall)
                Text("Remplace Google Discover (200MB -> 2MB)", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(12.dp))
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) { Text("🌤️ 18°C Angers"); Text("🔋 Optimisée"); Text("💾 1.2GB libérés") } }
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) { Text("Raccourcis"); Text("• Gallery Low\n• Files Low\n• Guard\n• Control") } }
            }
        }}
    }
}
