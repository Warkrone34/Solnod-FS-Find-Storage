package com.onyxera.fs

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.onyxera.fs.data.FSDatabase
import com.onyxera.fs.repository.MaterialRepository

/**
 * Uygulamanın en tepedeki yaşam döngüsü sınıfı.
 * Coil ImageLoaderFactory arayüzü eklenerek "Aşırı Isınma" ve "Kasılma" sorunları çözülmüştür.
 */
class FAndSApplication : Application(), ImageLoaderFactory {

    val repository by lazy {
        val db = FSDatabase.getDatabase(this)
        MaterialRepository(db.materialDao(), db.shipDao())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // RAM'in maksimum %15'ini kullan. (Telefonun boğulmasını ve uygulamanın çökmesini engeller)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    // Diskte en fazla %2'lik (yaklaşık 100-200MB) bir görsel tampon belleği ayır.
                    .maxSizePercent(0.02)
                    .build()
            }
            // Görsellerin yüklenirken aniden patlamaması için yumuşak geçiş.
            .crossfade(true)
            // Daha akıcı kaydırma (scrolling) performansı için yerel dosyalarda önbelleği zorla.
            .respectCacheHeaders(false)
            .build()
    }
}