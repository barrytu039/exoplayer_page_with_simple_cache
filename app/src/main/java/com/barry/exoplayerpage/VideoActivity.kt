package com.barry.exoplayerpage

import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.barry.kotlin_code_base.base.BaseActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File

class VideoActivity : BaseActivity() {

    private val cacheSize: Long = 3221225472L // 3GB
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var simpleCache: SimpleCache

    companion object {
        const val BUNDLE_VIDEO_URL = "BUNDLE_VIDEO_URL"
    }

    val url : String by lazyExtraData(BUNDLE_VIDEO_URL, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video)

        val defaultLoadControl = DefaultLoadControl.Builder()
                .setPrioritizeTimeOverSizeThresholds(false).build()

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setLoadControl(defaultLoadControl)
            .build()

        exoPlayerView.player = simpleExoPlayer

        val cacheFolder = File(this.filesDir, "media")

        val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)

        val dataBaseProvider = ExoDatabaseProvider(this)

        simpleCache = SimpleCache(cacheFolder, cacheEvictor, dataBaseProvider)

        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))

        val cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(simpleCache)
            setUpstreamDataSourceFactory(dataSourceFactory)
        }

        val videoSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        bufferingProgressBar.visibility = View.VISIBLE
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == PlaybackState.STATE_BUFFERING)
                    bufferingProgressBar.visibility = View.VISIBLE
                else
                    bufferingProgressBar.visibility = View.GONE
            }
        })

        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.setMediaSource(videoSource)
        simpleExoPlayer.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
        simpleCache.release()
    }
}