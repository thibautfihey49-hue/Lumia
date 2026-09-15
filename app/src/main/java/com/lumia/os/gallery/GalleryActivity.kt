package com.lumia.os.gallery

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MediaItem(val id: Long, val uri: android.net.Uri)

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { GalleryLow() } }
    }
}

@Composable
fun GalleryLow() {
    val ctx = LocalContext.current
    var items by remember { mutableStateOf<List<MediaItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val list = mutableListOf<MediaItem>()
            // LOW RAM: on ne charge que 200 dernières photos, pas 20k
            ctx.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                null, null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT 200"
            )?.use { c ->
                while (c.moveToNext()) {
                    val id = c.getLong(0)
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    list.add(MediaItem(id, uri))
                }
            }
            items = list
        }
    }

    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
        items(items, key = { it.id }) { item ->
            AsyncImage(model = item.uri, contentDescription = null, modifier = Modifier.size(120.dp).padding(2.dp), contentScale = ContentScale.Crop)
        }
    }
}
