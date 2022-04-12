package com.barry.exoplayerpage

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.barry.kotlin_code_base.base.BaseActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : BaseActivity() {

    private lateinit var simpleExoPlayer: ExoPlayer

    companion object {
        const val BUNDLE_VIDEO_URL = "BUNDLE_VIDEO_URL"
    }

    val url : String by lazyExtraData(BUNDLE_VIDEO_URL, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = AudioMediaServiceConnection.getInstance(this, ComponentName(this, AudioMediaService::class.java))
        connection.isConnected.observe(this) {
            if (it) {
                val bundle = Bundle()
                bundle.putString("title", "KPS Title")
                bundle.putString("album", "KPS Album")
                connection.transportControls.playFromUri(Uri.parse("https://kps-dev.thekono.com/api/v1/projects/61398d3c62cbe46b8b9e58af/streams/6255adcafac0a7000ef84c8d/playlist.m3u8"), bundle)
            }
        }

        setContentView(R.layout.activity_video)

//        val defaultLoadControl = DefaultLoadControl.Builder()
//                .setPrioritizeTimeOverSizeThresholds(false).build()
//
//        simpleExoPlayer = ExoPlayer.Builder(this)
//            .setLoadControl(defaultLoadControl)
//            .build()
//
//        exoPlayerView.player = simpleExoPlayer
//
//        val dataSourceFactory =
//            DefaultDataSource.Factory(this)
//
//        val cacheDataSourceFactory = CacheDataSource.Factory().apply {
//            setCache(AppApplication.createSimpleCache())
//            setUpstreamDataSourceFactory(dataSourceFactory)
//        }
//
//
//        val audioDataSource = HlsMediaSource.Factory(cacheDataSourceFactory)
//            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
//
//        bufferingProgressBar.visibility = View.VISIBLE
//        simpleExoPlayer.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(playbackState: Int) {
//                super.onPlaybackStateChanged(playbackState)
//                if (playbackState == Player.STATE_BUFFERING)
//                    bufferingProgressBar.visibility = View.VISIBLE
//                else
//                    bufferingProgressBar.visibility = View.GONE
//            }
//        })
//
//        simpleExoPlayer.playWhenReady = true
//        simpleExoPlayer.setMediaSource(audioDataSource)
//        simpleExoPlayer.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
//        simpleExoPlayer.release()
        AppApplication.releaseSimpleCache()
    }
}