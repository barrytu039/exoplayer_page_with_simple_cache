package com.barry.exoplayerpage

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData

class AudioMediaServiceConnection (val context: Context, serviceComponent: ComponentName) {
    private val TAG = "AudioMediaServiceConnection"

    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }
    val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(NOTHING_PLAYING) }
    val shuffleModeState = MutableLiveData<Int>()
        .apply { postValue(PlaybackStateCompat.SHUFFLE_MODE_NONE) }
    val repeatModeState = MutableLiveData<Int>()
        .apply { postValue(PlaybackStateCompat.REPEAT_MODE_NONE) }

    lateinit var mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }


    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        @SuppressLint("LongLogTag")
        override fun onConnected() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnected")
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        @SuppressLint("LongLogTag")
        override fun onConnectionSuspended() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnectionSuspended")
            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        @SuppressLint("LongLogTag")
        override fun onConnectionFailed() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnectionFailed")
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        @SuppressLint("LongLogTag")
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "MediaControllerCallback onPlaybackStateChanged:$state")
            val playbackStateCompat = state ?: EMPTY_PLAYBACK_STATE
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        @SuppressLint("LongLogTag")
        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            Log.d(TAG, "MediaControllerCallback onShuffleModeChanged:$shuffleMode")
            shuffleModeState.postValue(shuffleMode)
        }

        @SuppressLint("LongLogTag")
        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            Log.d(TAG, "MediaControllerCallback onRepeatModeChanged:$repeatMode")
            repeatModeState.postValue(repeatMode)
        }
    }

    companion object {
        // For Singleton instantiation.
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: AudioMediaServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: AudioMediaServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }
}


@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()