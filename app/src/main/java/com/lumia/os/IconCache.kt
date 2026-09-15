package com.lumia.os

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache

object IconCache {
    private val cache = LruCache<String, Drawable>(60)

    fun init(context: Context) {}

    fun get(key: String, loader: () -> Drawable): Drawable {
        return cache.get(key) ?: run {
            try {
                val d = loader()
                cache.put(key, d)
                d
            } catch (e: Exception) {
                // Fallback icône par défaut si une app a une icône corrompue
                cache.get("fallback") ?: loader()
            }
        }
    }
}
