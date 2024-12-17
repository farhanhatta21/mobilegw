package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import com.example.pblmobile.Models.UpdateResponse
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.Api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        val usernameEditText = findViewById<EditText>(R.id.etUsername)
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val nameTextView = findViewById<TextView>(R.id.tvProfileName)
        val saveButton = findViewById<TextView>(R.id.btnsave)
        val backButton = findViewById<TextView>(R.id.tvback)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("user_name", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""

        usernameEditText.setText(username)
        emailEditText.setText(email)
        passwordEditText.setText(password)
        nameTextView.text = username

        // Fungsi kembali
        backButton.setOnClickListener {
            val intent = Intent(this, Menuutama::class.java)
            startActivity(intent)
        }

        // Fungsi simpan
        saveButton.setOnClickListener {
            val updatedName = usernameEditText.text.toString()
            val updatedEmail = emailEditText.text.toString()
            val updatedPassword = passwordEditText.text.toString()

            val idAkun = sharedPreferences.getInt("id_akun", -1) // Default -1 jika tidak ada nilai
            if (idAkun == -1) {
                Toast.makeText(this, "ID akun tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi input
            if (updatedName.isEmpty() && updatedEmail.isEmpty() && updatedPassword.isEmpty()) {
                Toast.makeText(this, "Harap isi minimal salah satu: Nama, Email, atau Password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Siapkan request body
            val requestBody = mutableMapOf<String, Any>()
            requestBody["id_akun"] = idAkun.toString() // Pastikan id_akun dikirim sebagai String
            if (updatedName.isNotEmpty()) requestBody["nama"] = updatedName
            if (updatedEmail.isNotEmpty()) requestBody["email"] = updatedEmail
            if (updatedPassword.isNotEmpty()) requestBody["password"] = updatedPassword

            Log.d("DEBUG_UPDATE", "Request Body: $requestBody")

            // Panggil API untuk memperbarui data pengguna
            RetrofitClient.instance.updateUser(requestBody)
                .enqueue(object : Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        if (response.isSuccessful) {
                            val updateResponse = response.body()
                            if (updateResponse?.status == true) {
                                // Update data di SharedPreferences
                                with(sharedPreferences.edit()) {
                                    if (updatedName.isNotEmpty()) putString("user_name", updatedName)
                                    if (updatedEmail.isNotEmpty()) putString("email", updatedEmail)
                                    if (updatedPassword.isNotEmpty()) putString("password", updatedPassword)
                                    apply()
                                }

                                Toast.makeText(this@Profile, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Profile, Menuutama::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@Profile, updateResponse?.message ?: "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@Profile, "Failed to update profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                        Toast.makeText(this@Profile, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }





        // Fungsi untuk hide/unhide password
        passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // DrawableEnd = index 2
                val drawableWidth = passwordEditText.compoundDrawables[drawableEnd]?.bounds?.width() ?: 0
                if (event.rawX >= (passwordEditText.right - drawableWidth)) {
                    if (passwordEditText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                        // Tampilkan password
                        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        // Sembunyikan password
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    // Set cursor ke akhir teks
                    passwordEditText.setSelection(passwordEditText.text.length)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }
}
