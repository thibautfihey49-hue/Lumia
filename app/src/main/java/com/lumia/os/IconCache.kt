package com.lumia.os

import android.graphics.drawable.Drawable
import android.util.LruCache

object IconCache {
    // 60 icônes max = ~8MB RAM max. Xiaomi en garde 250 = 80MB
    private val cache = LruCache<String, Drawable>(60)
    fun get(key: String, load: () -> Drawable): Drawable = cache.get(key) ?: load().also { cache.put(key, it) }
    fun clear() = cache.evictAll()
}
