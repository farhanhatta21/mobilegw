package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Tentangaplikasi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tentangaplikasi)

        val back = findViewById<TextView>(R.id.tvback) // Use findViewById() directly

        back.setOnClickListener {
            // Start Tentangaplikasi activity
            val intent = Intent(this, Menuutama::class.java) // Use 'this' for activity context
            startActivity(intent)
        }

    }
}