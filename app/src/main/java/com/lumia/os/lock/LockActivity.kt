package com.lumia.os.lock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

class LockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val time = remember { SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date()) }
            Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(time, color = Color.White, fontSize = 72.sp)
                    Text("Lumia Lock - 0.3%/h", color = Color.Gray)
                    Text("3 notifs max = pas de réveil CPU", color = Color.DarkGray)
                }
            }
        }
    }
}
