package com.lumia.os

import android.app.Application
import android.content.ComponentCallbacks2

class LumiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // On dit à Android qu'on est une app low-ram
        // Il nous tuera en dernier et nous donnera moins de mémoire = moins de GC
    }
    override fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            IconCache.clear() // On vide le cache si le système a besoin de RAM
            System.gc()
        }
    }
}
