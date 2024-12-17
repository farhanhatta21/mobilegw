package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.LaporResponse
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminBelumDitanggapi : Fragment() {

    private lateinit var progressIndicator: LinearProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lapor, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLaporan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        progressIndicator = view.findViewById(R.id.linearProgressIndicator)
        progressIndicator.visibility = View.VISIBLE

        loadLaporanData(recyclerView)

        return view
    }

    private fun loadLaporanData(recyclerView: RecyclerView) {
        // Launch a coroutine to handle the network request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Network request on IO dispatcher
                val requestBody = mapOf("stats" to "Belum Ditanggapi")
                val response = RetrofitClient.instance.laporanadmin(requestBody).execute()

                // Update UI on the main thread
                withContext(Dispatchers.Main) {
                    progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        val laporanList = response.body()?.laporan ?: emptyList()
                        if (laporanList.isNotEmpty()) {
                            val adapter = AdminAdapter(laporanList) { laporan ->
                                // Long press action: Navigate to DetailLaporanActivity
                                val intent = Intent(requireContext(), DetailLaporanActivity::class.java)
                                intent.putExtra("LAPORAN_ID", laporan.id_lapor)
                                intent.putExtra("LAPORAN_NAMA", laporan.nama)
                                intent.putExtra("LAPORAN_LOKASI", laporan.lokasi)
                                intent.putExtra("LAPORAN_GAMBAR", laporan.bukti)
                                intent.putExtra("LAPORAN_STATUS", laporan.stats)
                                intent.putExtra("LATITUDE", laporan.latitude) // Add latitude
                                intent.putExtra("LONGITUDE", laporan.longitude) // Add longitude
                                startActivity(intent)
                            }
                            recyclerView.adapter = adapter
                        } else {
                            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main) {
                    progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
