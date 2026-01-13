package com.example.spotifyadskipper

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAcc = findViewById<Button>(R.id.btnAccessibility)
        val btnPocket = findViewById<Button>(R.id.btnPocketMode)
        
        val btnPlayPause = findViewById<ImageButton>(R.id.btnPlayPause)
        val btnNextTrack = findViewById<ImageButton>(R.id.btnNextTrack)
        val btnLike = findViewById<ImageButton>(R.id.btnLike)

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

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                val intent = Intent(this@MainActivity, PocketModeActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (event != null) {
            gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
        } else {
            super.onTouchEvent(event)
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