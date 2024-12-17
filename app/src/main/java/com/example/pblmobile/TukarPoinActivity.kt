package com.example.pblmobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.ReducePointsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TukarPoinActivity : AppCompatActivity() {

    private var lastSelectedTextView: TextView? = null // Stores the last selected TextView
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonTukarPoin: Button
    private var idAkun: Int = -1 // Variabel untuk menyimpan id_akun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tukarpoin)

        // Ambil id_akun dari SharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        idAkun = sharedPreferences.getInt("id_akun", -1)

        if (idAkun == -1) {
            Toast.makeText(this, "ID Akun tidak ditemukan, harap login kembali", Toast.LENGTH_SHORT).show()
            finish() // Tutup activity jika id_akun tidak ditemukan
            return
        }

        // Initialize views
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        buttonTukarPoin = findViewById(R.id.btntukarpoint)

        // List of TextViews for pulsa options
        val pulsaViews = listOf(
            Pair(findViewById<TextView>(R.id.tv_pulsa_5000), 5),
            Pair(findViewById<TextView>(R.id.tv_pulsa_10000), 10),
            Pair(findViewById<TextView>(R.id.tv_pulsa_15000), 15),
            Pair(findViewById<TextView>(R.id.tv_pulsa_20000), 20),
            Pair(findViewById<TextView>(R.id.tv_pulsa_25000), 25),
            Pair(findViewById<TextView>(R.id.tv_pulsa_30000), 30),
            Pair(findViewById<TextView>(R.id.tv_pulsa_50000), 50),
            Pair(findViewById<TextView>(R.id.tv_pulsa_100000), 100),
            Pair(findViewById<TextView>(R.id.tv_pulsa_200000), 200),
            Pair(findViewById<TextView>(R.id.tv_pulsa_500000), 500)
        )

        // Set OnClickListener for each pulsa option
        for ((textView, points) in pulsaViews) {
            textView.setOnClickListener {
                onPulsaSelected(textView, points)
            }
        }

        // Back button to navigate to another activity
        val poinsaya = findViewById<TextView>(R.id.tvback)
        poinsaya.setOnClickListener {
            val intent = Intent(this, Menuutama::class.java)
            startActivity(intent)
        }

        // Button to redeem points
        buttonTukarPoin.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString()
            val selectedPoints = lastSelectedTextView?.tag as? Int
            if (selectedPoints == null) {
                Toast.makeText(this, "Pilih pulsa terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Masukkan nomor telepon", Toast.LENGTH_SHORT).show()
            } else {
                // Tampilkan dialog konfirmasi dengan detail poin dan nomor telepon
                showTukarPoinDialog(phoneNumber, selectedPoints)
            }
        }
    }

    private fun showTukarPoinDialog(phoneNumber: String, selectedPoints: Int) {
        // Inflate layout custom dialog
        val dialogView = layoutInflater.inflate(R.layout.alert_tukarpoin, null)

        // Buat AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Agar tidak bisa ditutup dengan klik di luar
            .create()

        // Terapkan background bundar menggunakan rounded_putih.xml
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_putih)

        // Akses elemen di dalam layout dialog
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        // Set pesan konfirmasi dengan format .000
        dialogMessage.text = "Apakah Anda yakin ingin menukarkan ${selectedPoints}.000 poin untuk nomor $phoneNumber?"

        // Set action untuk tombol "Tidak"
        btnCancel.setOnClickListener {
            dialog.dismiss() // Tutup dialog
        }

        // Set action untuk tombol "Ya"
        btnConfirm.setOnClickListener {
            dialog.dismiss() // Tutup dialog
            redeemPoints(phoneNumber, selectedPoints)
        }

        // Tampilkan dialog
        dialog.show()
    }


    private fun onPulsaSelected(selectedTextView: TextView, points: Int) {
        // Reset previously selected TextView
        lastSelectedTextView?.let {
            it.setBackgroundResource(R.drawable.rounded_with_shadow) // Default background
            it.setTextColor(getColor(R.color.button_green)) // Default text color
        }

        // Highlight the selected TextView
        selectedTextView.setBackgroundResource(R.drawable.roundedijo) // Selected background
        selectedTextView.setTextColor(getColor(R.color.white)) // White text color
        selectedTextView.tag = points

        // Store the selected TextView
        lastSelectedTextView = selectedTextView
    }


    @SuppressLint("MissingInflatedId")
    private fun showAlertNotEnoughPoints() {
        // Inflate the custom alert dialog layout
        val dialogView = layoutInflater.inflate(R.layout.alert_poin_tidak_cukup, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Disable touch outside to close
            .create()

        // Access elements in the dialog
        val btnKembali = dialogView.findViewById<Button>(R.id.btn_kembali)

        // Set the dialog message if you want to customize it
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = "Point anda tidak cukup :("

        // Handle button click
        btnKembali.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }


    private fun redeemPoints(phoneNumber: String, points: Int) {
        val apiService = RetrofitClient.instance
        val reducePointsRequest = ReducePointsRequest(id_akun = idAkun.toString(), pengurangan = points)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.reducePoints(reducePointsRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let {
                            if (it.status) {
                                Toast.makeText(this@TukarPoinActivity, it.message, Toast.LENGTH_SHORT).show()
                                resetUI()
                                navigateToMenuUtama()
                            } else {

                                showAlertNotEnoughPoints()
                            }
                        }
                    } else {
                        Toast.makeText(this@TukarPoinActivity, "Gagal menukarkan poin", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TukarPoinActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun resetUI() {
        lastSelectedTextView?.let {
            it.setBackgroundResource(R.drawable.rounded_with_shadow)
            it.setTextColor(getColor(R.color.button_green))
        }
        lastSelectedTextView = null
        editTextPhoneNumber.text.clear()
    }

    private fun navigateToMenuUtama() {
        val intent = Intent(this, TukarPoinBerhasil::class.java)
        startActivity(intent)
        finish()
    }
}