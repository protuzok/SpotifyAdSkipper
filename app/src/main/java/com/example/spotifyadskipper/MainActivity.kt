package com.example.spotifyadskipper

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAcc = findViewById<Button>(R.id.btnAccessibility)
        val btnPocket = findViewById<Button>(R.id.btnPocketMode)

        // Логіка для переходу в налаштування
        btnAcc.setOnClickListener {
            val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnPocket.setOnClickListener {
            val intent = android.content.Intent(this, PocketModeActivity::class.java)
            startActivity(intent)
        }
    }
}