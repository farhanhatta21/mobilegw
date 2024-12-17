package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.UpdateAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailLaporanActivity : AppCompatActivity() {

    private lateinit var spinnerStatusDetail: Spinner
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var laporanStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)

        // Retrieve data from the intent
        val laporanId = intent.getIntExtra("LAPORAN_ID", 0)
        laporanStatus = intent.getStringExtra("LAPORAN_STATUS")
        val laporanNama = intent.getStringExtra("LAPORAN_NAMA")
        val laporanLokasi = intent.getStringExtra("LAPORAN_LOKASI")
        val laporanGambar = intent.getStringExtra("LAPORAN_GAMBAR")
        latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

        // Display the data
        findViewById<TextView>(R.id.tvNamaDetail).text = laporanNama
        findViewById<TextView>(R.id.tvLokasiDetail).text = laporanLokasi

        // Load image using Glide
        Glide.with(this)
            .load(laporanGambar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.gambartdktersedia)
            .into(findViewById(R.id.ivBuktiDetail))

        // Set up Spinner for status change
        spinnerStatusDetail = findViewById(R.id.spinnerStatusDetail)
        val statuses = arrayOf("Belum Ditanggapi", "Ditanggapi", "Selesai")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusDetail.adapter = adapter

        // Set the initial selected item
        spinnerStatusDetail.setSelection(statuses.indexOf(laporanStatus))

        // Listen for item selection in Spinner
        spinnerStatusDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newStatus = statuses[position]
                if (newStatus != laporanStatus) {
                    updateLaporanStatus(laporanId, newStatus)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set up the "View Location" button
        findViewById<Button>(R.id.buttonViewLocation).setOnClickListener {
            navigateToGoogleMaps()
        }
    }

    private fun updateLaporanStatus(idLapor: Int, newStatus: String) {
        val requestBody = mapOf(
            "id_lapor" to idLapor.toString(),
            "stats" to newStatus
        )

        RetrofitClient.instance.updateLaporanStatus(requestBody).enqueue(object : Callback<UpdateAdmin> {
            override fun onResponse(call: Call<UpdateAdmin>, response: Response<UpdateAdmin>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@DetailLaporanActivity, "Status updated: ${response.body()?.message}", Toast.LENGTH_SHORT).show()

                    // Update the status in the spinner after successful update
                    laporanStatus = newStatus
                    val statuses = arrayOf("Belum Ditanggapi", "Ditanggapi", "Selesai")
                    spinnerStatusDetail.setSelection(statuses.indexOf(newStatus))

                    // Intent to AdminActivity after successful status update
                    val intent = Intent(this@DetailLaporanActivity, AdminActivity::class.java)
                    startActivity(intent)
                    finish()  // Optionally finish the current activity
                } else {
                    Toast.makeText(this@DetailLaporanActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateAdmin>, t: Throwable) {
                Toast.makeText(this@DetailLaporanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToGoogleMaps() {
        android.util.Log.d("DetailLaporanActivity", "Navigating to Latitude: $latitude, Longitude: $longitude")
        if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
            // Prepare the geo URI
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude"

            // Create the intent to open Google Maps
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")

            // Check if Google Maps is installed
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle invalid location coordinates
            Toast.makeText(this, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
        }
    }
}
