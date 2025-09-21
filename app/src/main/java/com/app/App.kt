package com.app

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.SingletonImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

class App : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(500L * 1024 * 1024) // 500MB
                    .build()
            }
            .build()
    }
}