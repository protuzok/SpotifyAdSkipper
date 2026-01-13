package com.example.spotifyadskipper

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAcc = findViewById<Button>(R.id.btnAccessibility)
        val btnPocket = findViewById<Button>(R.id.btnPocketMode)
        
        val btnPlayPause = findViewById<ImageButton>(R.id.btnPlayPause)
        val btnNextTrack = findViewById<ImageButton>(R.id.btnNextTrack)
        val btnLike = findViewById<ImageButton>(R.id.btnLike)

        // Логіка для переходу в налаштування
        btnAcc.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnPocket.setOnClickListener {
            val intent = Intent(this, PocketModeActivity::class.java)
            startActivity(intent)
        }

        btnPlayPause.setOnClickListener {
            sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        }

        btnNextTrack.setOnClickListener {
            sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
        }

        btnLike.setOnClickListener {
            val intent = Intent("com.example.spotifyadskipper.ACTION_LIKE")
            sendBroadcast(intent)
            Toast.makeText(this, "Trying to save/like...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMediaKeyEvent(keyCode: Int) {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val eventTime = SystemClock.uptimeMillis()

        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }
}