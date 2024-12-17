package com.example.pblmobile

import android.content.Context
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DitanggapiFragment : Fragment() {

    private lateinit var progressIndicator: LinearProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lapor, container, false)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLaporan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup Progress Indicator
        progressIndicator = view.findViewById(R.id.linearProgressIndicator)
        progressIndicator.visibility = View.VISIBLE

        // Load Data
        loadLaporanData(recyclerView)

        return view
    }

    private fun loadLaporanData(recyclerView: RecyclerView) {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Ambil id_akun sebagai Integer
        val idAkun = sharedPreferences.getInt("id_akun", -1) // -1 sebagai default jika tidak ada nilai

        if (idAkun != -1) {
            val requestBody = mapOf(
                "id_akun" to idAkun.toString(), // Ubah menjadi String jika API mengharuskan
                "stats" to "Ditanggapi"
            )

            RetrofitClient.instance.getLaporanByStatus(requestBody).enqueue(object : Callback<LaporResponse> {
                override fun onResponse(call: Call<LaporResponse>, response: Response<LaporResponse>) {
                    progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        val laporanList = response.body()?.laporan ?: emptyList()
                        recyclerView.adapter = LaporAdapter(laporanList)
                    } else {
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LaporResponse>, t: Throwable) {
                    progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            progressIndicator.visibility = View.GONE
            Toast.makeText(requireContext(), "ID Akun tidak ditemukan. Harap login ulang.", Toast.LENGTH_SHORT).show()
        }
    }

}
