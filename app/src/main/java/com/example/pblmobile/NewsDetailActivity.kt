package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Retrieve data from the Intent
        val judul = intent.getStringExtra("judul")
        val gambar = intent.getStringExtra("gambar")
        val waktu = intent.getStringExtra("waktu")
        val deskripsi = intent.getStringExtra("deskripsi") // If available
        val detail = intent.getStringExtra("detail") // If available
        val backButton = findViewById<TextView>(R.id.tvback)

        backButton.setOnClickListener {
            val intent = Intent(this, Menuutama::class.java)
            startActivity(intent)
        }

        // Log the received image URL for debugging purposes
        Log.d("NewsDetailActivity", "Gambar URL: $gambar")

        // Find views and set the values
        val titleTextView: TextView = findViewById(R.id.textViewTitle)
        val imageView: ImageView = findViewById(R.id.imageViewGambar)
        val dateTextView: TextView = findViewById(R.id.textViewDate)
        val descriptionTextView: TextView = findViewById(R.id.textViewDeskripsi)
        val detailTextView: TextView = findViewById(R.id.textViewDetail)

        // Set the text content to the corresponding TextViews
        titleTextView.text = judul
        dateTextView.text = waktu
        descriptionTextView.text = deskripsi
        detailTextView.text = detail

        // Base URL for the images
        val baseUrl = "https://tkj-3b.com/api/kelompok_1/"

        // Log the complete image URL for debugging
        val imageUrl = baseUrl + (gambar ?: "")
        Log.d("NewsDetailActivity", "Full Image URL: $imageUrl")

        // Make sure that the URL is valid before loading the image
        if (!imageUrl.isNullOrEmpty()) {
            // Use Glide to load the image into the ImageView
            Glide.with(this)
                .load(imageUrl) // Load the image using the full URL
                .apply(RequestOptions().placeholder(R.drawable.loading)) // Placeholder image while loading
                .into(imageView)
        } else {
            Log.d("NewsDetailActivity", "Image URL is empty or invalid.")
        }
    }
}
