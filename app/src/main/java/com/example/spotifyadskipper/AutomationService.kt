package com.example.spotifyadskipper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.os.SystemClock
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutomationService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 1. Чекаємо появи екрана налаштувань або діалогу
        val packageName = event.packageName?.toString()

        if (packageName == "com.android.settings") {
            val rootNode = rootInActiveWindow ?: return

            // Шукаємо кнопку зупинки (Force stop)
            // Використовуй список текстів для підтримки різних мов
            val stopButtons = findNodesByTexts(rootNode, listOf("Force stop", "Зупинити", "Остановить"))

            for (node in stopButtons) {
                if (node.isEnabled) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return // Виходимо, щоб не клацати зайвого
                }
            }

            // 2. Шукаємо кнопку підтвердження (OK) у спливаючому вікні
            val okButtons = findNodesByTexts(rootNode, listOf("OK", "ОК", "Force stop", "Примусово зупинити"))
            for (node in okButtons) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                // Після успішної зупинки чекаємо 1 сек і запускаємо музику
                Thread {
                    Thread.sleep(2000)
                    restartMusic()
                }.start()
            }
        }
    }

    private fun findNodesByTexts(root: AccessibilityNodeInfo, texts: List<String>): List<AccessibilityNodeInfo> {
        val foundNodes = mutableListOf<AccessibilityNodeInfo>()
        for (text in texts) {
            val nodes = root.findAccessibilityNodeInfosByText(text)
            if (nodes != null) foundNodes.addAll(nodes)
        }
        return foundNodes
    }

    private fun restartMusic() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val eventTime = SystemClock.uptimeMillis()

        // Емуляція натискання медіа-кнопки Play
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    override fun onInterrupt() {}
}