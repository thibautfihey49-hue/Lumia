package com.lumia.os

import android.app.Application

class LumiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Précharge cache icônes en arrière-plan, 0 blocage UI
        IconCache.init(this)
    }
}
