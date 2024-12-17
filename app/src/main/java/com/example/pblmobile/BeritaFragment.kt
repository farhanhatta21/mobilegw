package com.example.pblmobile

import android.app.AlertDialog
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
import com.example.pblmobile.Models.BeritaResponse
import com.example.pblmobile.Models.BeritaItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beritaAdapter: BeritaAdapter
    private var beritaList: MutableList<BeritaItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_berita, container, false)
        recyclerView = binding.findViewById(R.id.rvLaporan)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        beritaAdapter = BeritaAdapter(requireContext(), beritaList)
        recyclerView.adapter = beritaAdapter

        // Call API to fetch berita
        fetchBerita()

        // Initialize Floating Action Button
        val fabSimpanBerita: FloatingActionButton = binding.findViewById(R.id.fabSimpanBerita)

        // Set onClickListener untuk FAB
        fabSimpanBerita.setOnClickListener {
            val intent = Intent(activity, SimpanBerita::class.java)
            startActivity(intent)
        }

        return binding
    }

    private fun fetchBerita() {
        val requestBody = emptyMap<String, String>()
        RetrofitClient.instance.getBerita(requestBody).enqueue(object : Callback<BeritaResponse> {
            override fun onResponse(call: Call<BeritaResponse>, response: Response<BeritaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val beritaResponse = response.body()!!
                    if (beritaResponse.status) {
                        // Update the beritaList and notify adapter
                        beritaList.clear()
                        beritaList.addAll(beritaResponse.berita)
                        beritaAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(activity, "Gagal memuat berita: ${beritaResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Response error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BeritaResponse>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
