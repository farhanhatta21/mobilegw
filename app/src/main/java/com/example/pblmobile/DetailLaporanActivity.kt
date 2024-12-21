package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.UpdateAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailLaporanActivity : AppCompatActivity() {

    private lateinit var spinnerStatusDetail: Spinner
    private var laporanStatus: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)

        // Retrieve data from Intent
        val laporanId = intent.getIntExtra("LAPORAN_ID", 0)
        laporanStatus = intent.getStringExtra("LAPORAN_STATUS")
        val laporanNama = intent.getStringExtra("LAPORAN_NAMA")
        val laporanLokasi = intent.getStringExtra("LAPORAN_LOKASI")
        val laporanGambar = intent.getStringExtra("LAPORAN_GAMBAR")
        latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

        // Display data
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
        spinnerStatusDetail.setSelection(statuses.indexOf(laporanStatus))

        // Handle status change
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
                    laporanStatus = newStatus
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
        if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
        }
    }
}
