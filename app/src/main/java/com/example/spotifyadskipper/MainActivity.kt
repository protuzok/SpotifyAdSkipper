package com.example.spotifyadskipper

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnToggle)
        val btnAcc = findViewById<Button>(R.id.btnAccessibility) // Знаходимо нову кнопку
        val btnSpotify = findViewById<Button>(R.id.btnOpenSpotify)

        updateButtonUI(btn)

        btn.setOnClickListener {
            AppConfig.isEnabled = !AppConfig.isEnabled
            updateButtonUI(btn)
        }

        // Логіка для переходу в налаштування
        btnAcc.setOnClickListener {
            val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnSpotify.setOnClickListener {
            val packageName = "com.spotify.music" // Стандартне ім'я пакета Spotify
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            if (intent != null) {
                startActivity(intent)
            } else {
                // Якщо додаток не встановлено, можна відкрити його в Play Store
                val marketIntent = android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("market://details?id=$packageName")
                )
                startActivity(marketIntent)
            }
        }
    }

    private fun updateButtonUI(btn: Button) {
        if (AppConfig.isEnabled) {
            btn.text = "СКРИПТ ПРАЦЮЄ !!! (НАТИСНИ ЩОБ ЗУПИНИТИ)"
            btn.setBackgroundColor(Color.RED)
        } else {
            btn.text = "СКРИПТ ВИМКНЕНО (НАТИСНИ ЩОБ ЗАПУСТИТИ)"
            btn.setBackgroundColor(Color.GREEN)
        }
    }
}