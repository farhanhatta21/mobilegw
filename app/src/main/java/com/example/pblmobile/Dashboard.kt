package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.BeritaResponse
import com.example.pblmobile.Models.LaporResponse
import com.example.pblmobile.Models.UpdateResponse
import com.example.pblmobile.Models.PointResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class Dashboard : Fragment() {

    private lateinit var dashboardRecyclerView: RecyclerView
    private lateinit var dashboardAdapter: DashboardAdapter
    private val dashboardItems = mutableListOf<DashboardItem>()

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val eventItems = mutableListOf<EventItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard2, container, false)

        // Setup RecyclerView for Dashboard items
        dashboardRecyclerView = view.findViewById(R.id.recyclerView)
        dashboardRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        dashboardAdapter = DashboardAdapter(dashboardItems)
        dashboardRecyclerView.adapter = dashboardAdapter


        // Setup RecyclerView for event items
        eventRecyclerView = view.findViewById(R.id.recyclerViewEvents)
        eventAdapter = EventAdapter(eventItems) { eventItem ->
            val intent = Intent(requireContext(), NewsDetailActivity::class.java)
            intent.putExtra("judul", eventItem.judul)
            intent.putExtra("gambar", eventItem.gambar)
            intent.putExtra("waktu", eventItem.waktu)
            intent.putExtra("deskripsi", eventItem.deskripsi)
            intent.putExtra("detail", eventItem.detail)
            startActivity(intent)
        }

        eventRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        eventRecyclerView.adapter = eventAdapter

        // Load data into both RecyclerViews
        loadDashboardItems()
        loadEventItems()

        // Update user points from API
        updateUserPoints()

        // Display user name and point from SharedPreferences
        displayUserName(view)
        displayUserPoint(view)

        // Setup click listener for "Poin Saya"
        val poinsaya = view.findViewById<TextView>(R.id.textView15)
        poinsaya.setOnClickListener {
            val intent = Intent(requireContext(), TukarPoinActivity::class.java)
            startActivity(intent)
        }

        // Floating Action Button for reporting
        val fabReport = view.findViewById<FloatingActionButton>(R.id.fabReport)
        fabReport.setOnClickListener {
            val intent = Intent(requireContext(), Pelaporan::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadDashboardItems() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val idAkun = sharedPreferences.getInt("id_akun", -1)

        if (idAkun == -1) {
            Log.e("Dashboard", "ID akun tidak ditemukan di SharedPreferences")
            return
        }

        val requestBody = mapOf("id_akun" to idAkun)
        RetrofitClient.instance.getLaporan(requestBody).enqueue(object : Callback<LaporResponse> {
            override fun onResponse(call: Call<LaporResponse>, response: Response<LaporResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    dashboardItems.clear()
                    response.body()?.laporan?.forEach { laporan ->
                        dashboardItems.add(
                            DashboardItem(
                                title = laporan.lokasi,
                                subtitle = laporan.stats,
                                date = laporan.tanggal
                            )
                        )
                    }
                    dashboardAdapter.notifyDataSetChanged()

                    // Scroll ke item terakhir
                    dashboardRecyclerView.scrollToPosition(dashboardItems.size - 1)
                } else {
                    Log.e("Dashboard", "Gagal memuat laporan: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<LaporResponse>, t: Throwable) {
                Log.e("Dashboard", "Error: ${t.message}")
            }
        })
    }

    private fun loadEventItems() {
        val requestBody = mapOf("key" to "value") // Customize request if necessary

        RetrofitClient.instance.getBerita(requestBody).enqueue(object : Callback<BeritaResponse> {
            override fun onResponse(call: Call<BeritaResponse>, response: Response<BeritaResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val beritaList = response.body()?.berita ?: emptyList()

                    // Clear existing items and add new ones from the API response
                    eventItems.clear()
                    beritaList.forEach { berita ->
                        eventItems.add(
                            EventItem(
                                gambar = berita.gambar,
                                judul = berita.judul,
                                deskripsi = berita.deskripsi,
                                detail = berita.detail,
                                waktu = berita.waktu
                            )
                        )
                    }

                    // Notify the adapter about data change
                    eventAdapter.notifyDataSetChanged()

                    // Handle item click to open NewsDetailActivity
                    eventAdapter = EventAdapter(eventItems) { eventItem ->
                        val intent = Intent(requireContext(), NewsDetailActivity::class.java)
                        intent.putExtra("judul", eventItem.judul)
                        intent.putExtra("gambar", eventItem.gambar)
                        intent.putExtra("waktu", eventItem.waktu)
                        intent.putExtra("deskripsi", eventItem.deskripsi)
                        intent.putExtra("detail", eventItem.detail)
                        startActivity(intent)
                    }


                } else {
                    Log.e("Dashboard", "Gagal memuat berita: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<BeritaResponse>, t: Throwable) {
                Log.e("Dashboard", "Error memuat berita: ${t.message}")
            }
        })
    }





    private fun updateUserPoints() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val idAkun = sharedPreferences.getInt("id_akun", -1)

        if (idAkun == -1) {
            Log.e("Dashboard", "ID akun tidak ditemukan di SharedPreferences")
            return
        }

        val requestBody = mapOf("id_akun" to idAkun)

        RetrofitClient.instance.updatePoints(requestBody).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val newPoints = response.body()?.newPoints ?: 0
                    Log.d("Dashboard", "Poin berhasil diperbarui: $newPoints")

                    val sharedPreferencesEditor = sharedPreferences.edit()
                    val currentPoints =
                        sharedPreferences.getString("point", "0.000")?.toBigDecimalOrNull()
                            ?: BigDecimal.ZERO
                    val updatedPoints = currentPoints.add(BigDecimal(newPoints))
                    sharedPreferencesEditor.putString("point", updatedPoints.toPlainString())
                    sharedPreferencesEditor.apply()

                    displayUserPoint(requireView()) // Refresh tampilan poin
                } else {
                    Log.e("Dashboard", "Gagal memperbarui poin: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Log.e("Dashboard", "Error memperbarui poin: ${t.message}")
            }
        })
    }

    private fun displayUserName(view: View) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", "User")

        val userNameTextView = view.findViewById<TextView>(R.id.textView19)
        userNameTextView.text = "Hi, $userName"
    }

    private fun displayUserPoint(view: View) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val idAkun = sharedPreferences.getInt("id_akun", -1)

        if (idAkun == -1) {
            Log.e("Dashboard", "ID akun tidak ditemukan di SharedPreferences")
            return
        }

        val requestBody = mapOf("id_akun" to idAkun)

        // Panggil API untuk mendapatkan poin
        RetrofitClient.instance.getUserPoints(requestBody).enqueue(object : Callback<PointResponse> {
            override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val point = response.body()?.data?.point ?: "0"
                    val pointTextView = view.findViewById<TextView>(R.id.tvpoint)
                    pointTextView.text = point

                    // Simpan poin ke SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.putString("point", point)
                    editor.apply()
                } else {
                    Log.e("Dashboard", "Gagal mendapatkan poin: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                Log.e("Dashboard", "Error: ${t.message}")
            }
        })
    }

}
