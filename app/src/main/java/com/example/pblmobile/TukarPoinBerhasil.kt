package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.databinding.ActivityTukarPoinBerhasilBinding

class TukarPoinBerhasil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTukarPoinBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnKemDash.setOnClickListener {
            // Navigate to DashboardFragment
            val intent = Intent(this, Menuutama::class.java)
            startActivity(intent)
        }

        // Button to go to TukarPoinActivity
        binding.btnPoinLagi.setOnClickListener {
            val intent = Intent(this, TukarPoinActivity::class.java)
            startActivity(intent)
        }
    }
}
