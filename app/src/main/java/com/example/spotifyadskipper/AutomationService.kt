package com.example.spotifyadskipper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutomationService : AccessibilityService() {
    companion object { var shouldKill = false }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!shouldKill || !AppConfig.isEnabled) return

        val rootNode = rootInActiveWindow ?: return
        val stopButtons = rootNode.findAccessibilityNodeInfosByText("Зупинити")
            ?: rootNode.findAccessibilityNodeInfosByText("Force stop")

        for (node in stopButtons) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                Handler(Looper.getMainLooper()).postDelayed({
                    confirmAndRestart()
                    shouldKill = false
                }, 300)
            }
        }
    }

    private fun confirmAndRestart() {
        // 1. Клікаємо ОК у діалозі
        rootInActiveWindow?.findAccessibilityNodeInfosByText("OK")
            ?.firstOrNull()?.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        // 2. Чекаємо 1 секунду, поки процес завершиться, і «будимо» музику
        Handler(Looper.getMainLooper()).postDelayed({
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val event = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
            audioManager.dispatchMediaKeyEvent(event)

            // 3. Повертаємося назад з налаштувань на головний екран
            performGlobalAction(GLOBAL_ACTION_BACK)
        }, 1000)
    }

    override fun onInterrupt() {}
}