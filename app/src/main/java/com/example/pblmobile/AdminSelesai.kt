package com.example.pblmobile

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
import com.example.pblmobile.Models.Laporan
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminSelesai : Fragment() {

    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var adapter: AdminAdapter

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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = mapOf("stats" to "Selesai")
                val response = RetrofitClient.instance.laporanadmin(requestBody).execute()

                withContext(Dispatchers.Main) {
                    progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        val laporanList = response.body()?.laporan?.toMutableList() ?: mutableListOf()
                        if (laporanList.isNotEmpty()) {
                            adapter = AdminAdapter(
                                laporanList,
                                onItemEditListener = { laporan ->
                                    // Navigate to DetailLaporanActivity
                                    val intent = Intent(requireContext(), DetailLaporanActivity::class.java)
                                    intent.putExtra("LAPORAN_ID", laporan.id_lapor)
                                    intent.putExtra("LAPORAN_NAMA", laporan.nama)
                                    intent.putExtra("LAPORAN_LOKASI", laporan.lokasi)
                                    intent.putExtra("LAPORAN_GAMBAR", laporan.bukti)
                                    intent.putExtra("LAPORAN_STATUS", laporan.stats)
                                    intent.putExtra("LATITUDE", laporan.latitude)
                                    intent.putExtra("LONGITUDE", laporan.longitude)
                                    startActivity(intent)
                                },
                                onItemDeleteListener = { laporan, position ->
                                    deleteLaporan(laporan.id_lapor.toString()) { success ->
                                        if (success) {
                                            adapter.removeItem(position)
                                            Toast.makeText(requireContext(), "Laporan dihapus", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "Gagal menghapus laporan", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                            recyclerView.adapter = adapter
                        } else {
                            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteLaporan(idLapor: String, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = mapOf("id_lapor" to idLapor)
                val response = RetrofitClient.instance.deleteLaporan(requestBody).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
        }
    }
}
