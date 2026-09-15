package com.lumia.os.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

class LumiaKeyboard : InputMethodService() {
    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setContent { KeyboardLow() }
        }
    }

    @Composable
    fun KeyboardLow() {
        val keys = listOf("a","z","e","r","t","y","u","i","o","p","q","s","d","f","g","h","j","k","l","m","w","x","c","v","b","n")
        var text by remember { mutableStateOf("") }
        Column(Modifier.fillMaxWidth().height(220.dp)) {
            LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                items(keys) { k ->
                    Button(onClick = { currentInputConnection.commitText(k, 1) }, modifier = Modifier.padding(2.dp)) { Text(k) }
                }
            }
            Row {
                Button(onClick = { currentInputConnection.commitText(" ", 1) }) { Text("ESPACE") }
                Button(onClick = { currentInputConnection.deleteSurroundingText(1,0) }) { Text("⌫") }
            }
        }
    }
}
