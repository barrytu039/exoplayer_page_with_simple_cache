package com.barry.exoplayerpage

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*

const val AUDIO_MEDIA_CHANNEL_ID = "com.barrytu.exoplayer.audio"
const val AUDIO_MEDIA_NOTIFICATION_ID = 0x123 // Arbitrary number used to identify our notification

class AudioMediaNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    val titleListener: () -> String,
    val albumListener: () -> String
) : CoroutineScope by MainScope()  {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(context, AUDIO_MEDIA_NOTIFICATION_ID, AUDIO_MEDIA_CHANNEL_ID)
            .setChannelNameResourceId(R.string.audio_channel_name)
            .setChannelDescriptionResourceId(R.string.audio_channel_desc)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListener)
            .build().apply {
                setMediaSessionToken(sessionToken)
            }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun getCurrentSubText(player: Player): CharSequence? {
            return super.getCurrentSubText(player)
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) : CharSequence {
            return player.mediaMetadata.albumTitle.toString()
        }

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return player.mediaMetadata.displayTitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    } ?: run {
                        getDefaultBitmap(context)
                    }

                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on downloading artwork.
                Glide.with(context).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
            }
        }

        private fun getDefaultBitmap(context: Context): Bitmap? {
            return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
                ?.toBitmap()
        }
    }
}

private const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private val glideOptions = RequestOptions()
    .fallback(R.drawable.ic_launcher_foreground)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)