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
            // 1. Повертаємось із налаштувань
            performGlobalAction(GLOBAL_ACTION_BACK)

            Thread {
                // 2. Чекаємо довше (1.5 сек), поки система закриє вікно налаштувань і "оживе"
                Thread.sleep(1500)

                // 3. Відправляємо команду Play
                restartMusic()

                // 4. ЗНАЧНО ЗБІЛЬШЕНА ПАУЗА (2 - 2.5 секунди)
                // Spotify потрібно встигнути: завантажити трек, створити сесію та почати потік
                Thread.sleep(3500)

                // 5. Тепер перемикаємо на наступний трек
                skipToNextTrack()
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