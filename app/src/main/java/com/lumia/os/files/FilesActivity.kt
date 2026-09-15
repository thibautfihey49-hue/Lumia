package com.lumia.os.files

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FilesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Lumia Files Low", style = MaterialTheme.typography.headlineSmall)
                Text("1.5MB vs 80MB Files MIUI", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(12.dp))
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) { Text("Stockage interne"); Text("Download"); Text("DCIM"); Text("Documents") } }
            }
        }}
    }
}
