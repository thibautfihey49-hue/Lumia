package com.lumia.os.files

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

class FilesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { FilesLow() } }
    }
}

@Composable
fun FilesLow() {
    var path by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
    var files by remember { mutableStateOf(path.listFiles()?.sortedBy {!it.isDirectory }?: emptyList()) }

    LaunchedEffect(path) {
        files = path.listFiles()?.sortedWith(compareBy({!it.isDirectory }, { it.name.lowercase() }))?: emptyList()
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text(path.absolutePath, style = MaterialTheme.typography.labelSmall)
        LazyColumn {
            items(files, key = { it.absolutePath }) { f ->
                ListItem(headlineContent = { Text(f.name) }, supportingContent = { Text(if(f.isDirectory) "Dossier" else "${f.length()/1024} KB") }, modifier = Modifier.clickable {
                    if (f.isDirectory) path = f
                })
                Divider()
            }
        }
    }
}
