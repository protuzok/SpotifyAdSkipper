package com.example.spotifyadskipper

import android.app.Notification
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class AdDetectorService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Виправлено пакет на стандартний для Spotify
        if (sbn.packageName != "com.spotify.music") return

        val extras = sbn.notification.extras
        val token = extras.getParcelable<MediaSession.Token>(Notification.EXTRA_MEDIA_SESSION)

        if (token != null) {
            val controller = MediaController(this, token)
            val metadata = controller.metadata
            val state = controller.playbackState // Отримуємо стан відтворення

            if (metadata != null) {
                if (isThisAnAd(metadata, state)) {
                    Log.d("SpotifyAdDebug", "Детектовано РЕКЛАМУ. Запускаю ліквідацію...")
                    killSpotify()
                }
            }
        }
    }

    private fun isThisAnAd(metadata: MediaMetadata, state: PlaybackState?): Boolean {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)

        // 1. Базові текстові та часові маркери
        if (artist.isEmpty() || artist.lowercase() == "spotify") return true
        if (title.lowercase().contains("advertisement") || title.lowercase().contains("реклама")) return true
        if (duration <= 0) return true

        // 2. Аналіз дозволених дій (PlaybackState Actions)
        state?.let {
            val actions = it.actions

            // Перевіряємо, чи заборонено перемотування (ACTION_SEEK_TO)
            // та перехід на попередній трек (ACTION_SKIP_TO_PREVIOUS)
            val cannotSeek = (actions and PlaybackState.ACTION_SEEK_TO) == 0L
            val cannotSkipBack = (actions and PlaybackState.ACTION_SKIP_TO_PREVIOUS) == 0L

            // Якщо обидві дії заборонені і тривалість відповідає рекламі
            if (cannotSeek && cannotSkipBack && duration <= 30500) {
                return true
            }
        }

        return false
    }

    private fun killSpotify() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", "com.spotify.music", null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}