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
        if (event.packageName == "com.android.settings" && ServiceState.isAdDetected) {
            val rootNode = rootInActiveWindow ?: return

            // 1. ПРІОРИТЕТ: Спочатку шукаємо кнопку "ОК" (підтвердження)
            // Якщо вона є на екрані, значить діалог вже відкритий
            val okButtons = findNodesByTexts(rootNode, listOf("OK", "ОК", "Примусово зупинити", "Force stop"))
            // Примітка: у деяких версіях Android кнопка в діалозі називається так само, як і перша

            for (node in okButtons) {
                // Перевіряємо, чи це кнопка всередині діалогу (зазвичай вони не мають багато тексту навколо)
                // Або просто намагаємося натиснути, якщо це OK
                if (node.text?.toString()?.uppercase() == "OK" || node.text?.toString()?.uppercase() == "ОК") {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    ServiceState.isAdDetected = false // Тільки тут скидаємо статус

                    // Тепер можна повертатися назад, Spotify точно вбито
                    goBackAndRestart()
                    return
                }
            }

            // 2. Якщо кнопки ОК не знайдено, шукаємо основну кнопку "Зупинити"
            val stopButtons = findNodesByTexts(rootNode, listOf("Force stop", "Зупинити", "Остановить", "Примусово зупинити"))
            for (node in stopButtons) {
                if (node.isEnabled) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    // ПІСЛЯ ЦЬОГО НАТИСКАННЯ НІЧОГО НЕ РОБИМО.
                    // Чекаємо наступної події AccessibilityEvent, коли з'явиться діалог.
                    return
                }
            }
        }
    }

    private fun goBackAndRestart() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            performGlobalAction(GLOBAL_ACTION_BACK)

            Thread {
                Thread.sleep(1000) // Чекаємо, поки система "прокинеться"
                restartMusic()     // Запускаємо відтворення

                // --- НОВИЙ КОД ТУТ ---
                Thread.sleep(400)  // Коротка пауза (доля секунди) перед перемиканням
                skipToNextTrack()  // Натискаємо "Наступний трек"
                // ---------------------

                Thread.sleep(500)
                performGlobalAction(GLOBAL_ACTION_BACK)
            }.start()
        }, 300)
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
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val eventTime = SystemClock.uptimeMillis()

        // Емуляція натискання медіа-кнопки Play
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    private fun skipToNextTrack() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val eventTime = SystemClock.uptimeMillis()

        // Емуляція натискання клавіші "Наступний трек"
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    override fun onInterrupt() {}
}