package com.lumia.os

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache

object IconCache {
    private val cache = LruCache<String, Drawable>(150)
    fun get(pm: PackageManager, packageName: String, load: () -> Drawable): Drawable {
        return cache.get(packageName) ?: load().also { cache.put(packageName, it) }
    }
}
