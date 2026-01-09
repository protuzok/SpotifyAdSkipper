package com.example.spotifyadskipper

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class AdDetectorService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Якщо ти вимкнув скрипт у додатку — нічого не робимо
        if (!AppConfig.isEnabled) return

        if (sbn.packageName == "com.spotify.music") {
            val isAd = sbn.notification.actions == null || sbn.notification.actions.size < 3
            if (isAd) {
                // Переходимо до зупинки
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", "com.spotify.music", null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                AutomationService.shouldKill = true
            }
        }
    }
}