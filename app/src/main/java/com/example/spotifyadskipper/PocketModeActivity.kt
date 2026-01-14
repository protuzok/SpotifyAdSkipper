package com.example.spotifyadskipper

import android.os.Bundle

import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class PocketModeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocket_mode)

        // 1. Keep Screen On logic is already in XML (android:keepScreenOn="true"),
        // but we reinforce it here just in case.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 2. Set brightness to minimum
        setMinimalBrightness()

        // 3. Hide System Bars (Immersive Mode)
        hideSystemUI()

        // 4. Setup Long Press Listener

    }

    private fun setMinimalBrightness() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.01f
        window.attributes = layoutParams
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    // Triple tap variables
    private var tapCount = 0
    private var lastTapTime: Long = 0
    private val TAP_THRESHOLD = 400L // ms between taps

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime < TAP_THRESHOLD) {
                tapCount++
            } else {
                tapCount = 1
            }
            lastTapTime = currentTime

            if (tapCount >= 3) {
                Toast.makeText(applicationContext, "Exiting Pocket Mode", Toast.LENGTH_SHORT).show()
                finish()
                tapCount = 0 // Reset
            }
            return true
        }
        return super.onTouchEvent(event)
    }
    
    // If the window loses focus (e.g. Settings opened), and then we come back:
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}
