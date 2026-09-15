package com.lumia.os.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { 
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Lumia Gallery Low", style = MaterialTheme.typography.headlineSmall)
                Text("LIMIT 200 photos - 0 scan complet", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(12.dp))
                Text("✅ 3MB vs 250MB Gallery MIUI")
                Text("✅ Pas de cloud, local only")
                Text("✅ Cache 60 miniatures")
            }
        }}
    }
}
