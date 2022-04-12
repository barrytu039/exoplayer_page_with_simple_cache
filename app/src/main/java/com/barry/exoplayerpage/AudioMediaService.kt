package com.barry.exoplayerpage

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class AudioMediaService : MediaBrowserServiceCompat(), CoroutineScope by MainScope() {
    private val TAG = AudioMediaService::class.java.simpleName

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var notificationManager: AudioMediaNotificationManager
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
    private var isForegroundService = false

    private var title: String = ""
    private var album: String = ""

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(musicAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    private val playerListener = PlayerEventListener()

    private lateinit var currentPlayer: Player

    private val musicAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val titleListener : () -> String = {
        title
    }

    private val albumListener : () -> String = {
        album
    }

    private val dataSourceFactory: DefaultDataSource.Factory by lazy {
        DefaultDataSource.Factory(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(this, TAG)
            .apply {
                // Enable callbacks from MediaButtons and TransportControls
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )
                // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
                stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                // MySessionCallback() has methods that handle callbacks from a media controller
                setCallback(object : MediaSessionCompat.Callback() {
                    // todo: override callback method
                })

                setPlaybackState(stateBuilder.build())
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        sessionToken = mediaSession.sessionToken

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(AudioPlaybackPreparer())
//        mediaSessionConnector.setQueueNavigator(AudioQueueNavigator(mediaSession))

        switchToPlayer(previousPlayer = null, newPlayer = exoPlayer)

        notificationManager = AudioMediaNotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener(),
            titleListener,
            albumListener
        )

        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        //Save recent play info
        super.onTaskRemoved(rootIntent)

        currentPlayer.stop()
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.stop()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                // todo: prepare media item
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop(/* reset= */true)
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): BrowserRoot {
        return if (allowBrowsing(clientPackageName)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(KPS_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(KPS_MEDIA_EMPTY_ROOT, null)
        }
    }

    private fun allowBrowsing(clientPackageName: String): Boolean {
        return clientPackageName == packageName
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

    }

    private inner class AudioPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onPrepareFromSearch(
            query: String, playWhenReady: Boolean,
            extras: Bundle?
        ) = Unit

        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            return false
        }

        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_URI

        override fun onPrepareFromMediaId(
            mediaId: String, playWhenReady: Boolean,
            extras: Bundle?
        ) {

        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            title = extras?.getString("title", "unKnow")?: "unKnow"
            album = extras?.getString("album", "unknow")?: "unknow"
            currentPlayer.playWhenReady = true
            currentPlayer.stop()
            if (currentPlayer == exoPlayer) {
                val cacheDataSourceFactory = CacheDataSource.Factory().apply {
                    setCache(AppApplication.createSimpleCache())
                    setUpstreamDataSourceFactory(dataSourceFactory)
                }
                val mediaItem = MediaItem.Builder().setUri(uri)
                    .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle(title).setAlbumTitle(album).build())
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()

                val audioDataSource = HlsMediaSource.Factory(cacheDataSourceFactory)
                    .createMediaSource(mediaItem)

                if (exoPlayer.mediaItemCount == 0) {
                    exoPlayer.addMediaSource(audioDataSource)
                    exoPlayer.playWhenReady = true
                    exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                    exoPlayer.prepare()
                } else {
                    exoPlayer.prepare(audioDataSource)
                }

            }
        }

        override fun onPrepare(playWhenReady: Boolean) = Unit
    }

    private inner class AudioQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
            currentPlaylistItems[windowIndex].description
    }

    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@AudioMediaService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(currentPlayer)
                    // If playback is paused we remove the foreground state which allows the
                    // notification to be dismissed. An alternative would be to provide a "close"
                    // button in the notification which stops playback and clears the notification.
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) stopForeground(false)
                    }
                }
                else -> {
//                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            var message = "error"
            // todo: handle error
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePlayPause() {
        if (mediaSession.controller.playbackState.isPlaying) {
            mediaSession.controller.transportControls.pause()
        } else {
            mediaSession.controller.transportControls.play()
        }
    }

    private fun handleSkipNext() {
        mediaSession.controller.transportControls.skipToNext()
    }

    private fun handleSkipPrevious() {
        mediaSession.controller.transportControls.skipToPrevious()
    }
}

const val KPS_MEDIA_ROOT_ID = "/"
const val KPS_MEDIA_EMPTY_ROOT = "@empty@"


/**
 * Useful extension methods for [PlaybackStateCompat].
 */
inline val PlaybackStateCompat.isPrepared
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING) ||
            (state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.isPlaying
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING)

inline val PlaybackStateCompat.isPlayEnabled
    get() = (actions and PlaybackStateCompat.ACTION_PLAY != 0L) ||
            ((actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) &&
                    (state == PlaybackStateCompat.STATE_PAUSED))


/**
 * Calculates the current playback position based on last update time along with playback
 * state and speed.
 */
inline val PlaybackStateCompat.currentPlayBackPosition: Long
    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else {
        position
    }