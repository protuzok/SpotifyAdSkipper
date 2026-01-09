package com.example.spotifyadskipper

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Встановлюємо наш XML макет
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnToggle)

        // Оновлюємо вигляд кнопки відповідно до стану AppConfig
        updateButtonUI(btn)

        btn.setOnClickListener {
            // Змінюємо стан: якщо було true -> стане false, і навпаки
            AppConfig.isEnabled = !AppConfig.isEnabled
            updateButtonUI(btn)
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