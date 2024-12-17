package com.example.pblmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is completed
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val onboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // If onboarding is not completed, start OnboardingActivity
            val intent = Intent(this, OnboardingActivity::class.java) // <-- Fixed to OnboardingActivity
            startActivity(intent)
            finish() // Close this activity so the user can't go back to it
            return
        }

        // Continue loading MainActivity as usual
        setContentView(R.layout.activity_main)

        val text = SpannableString("SampahKu")
        text.setSpan(
            ForegroundColorSpan(Color.parseColor("#477871")),
            0,
            6,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text.setSpan(
            ForegroundColorSpan(Color.YELLOW),
            6,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textView = findViewById<TextView>(R.id.textView)
        textView.text = text

        // Get references to the buttons
        val masukButton = findViewById<Button>(R.id.button)
        val daftarButton = findViewById<Button>(R.id.button2)

        // Set click listeners for the buttons
        masukButton.setOnClickListener {
            // Start MasukActivity
            val intent = Intent(this, MasukActivity::class.java)
            startActivity(intent)
        }

        daftarButton.setOnClickListener {
            // Start BuatakunActivity
            val intent = Intent(this, BuatakunActivity::class.java)
            startActivity(intent)
        }
    }
}
