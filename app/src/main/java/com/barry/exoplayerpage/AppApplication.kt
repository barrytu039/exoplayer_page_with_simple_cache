package com.barry.exoplayerpage

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

class AppApplication : Application() {

    companion object {
        private lateinit var appContext : Context
        private var simpleCache : SimpleCache? = null
        private val cacheSize = 2L * 1024L * 1024L * 1024L // 2GB

        fun getAppContext() : Context {
            return appContext
        }

        fun createSimpleCache() : SimpleCache {
            simpleCache?.release()
            val databaseProvider: DatabaseProvider = ExoDatabaseProvider(getAppContext())
            simpleCache = SimpleCache(File(getAppContext().cacheDir, "exo_cache"), LeastRecentlyUsedCacheEvictor(cacheSize), databaseProvider)
            return simpleCache!!
        }

        fun releaseSimpleCache() {
            simpleCache?.release()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

}