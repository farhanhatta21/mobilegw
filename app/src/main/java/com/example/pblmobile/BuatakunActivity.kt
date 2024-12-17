package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuatakunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buatakun)

        // Get references to the views
        val nameEditText = findViewById<EditText>(R.id.editTextText3)
        val emailEditText = findViewById<EditText>(R.id.editTextText4)
        val passwordEditText = findViewById<EditText>(R.id.editTextText7)
        val createAccountButton = findViewById<Button>(R.id.button3)
        val tologin = findViewById<TextView>(R.id.tologin)

        // Set click listener for the button
        createAccountButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API
            val requestBody = mapOf(
                "nama" to name,
                "email" to email,
                "password" to password
            )

            RetrofitClient.instance.registerUser(requestBody)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.status) {
                                // Success, navigate to login
                                Toast.makeText(this@BuatakunActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@BuatakunActivity, MasukActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Error from API (Email already registered)
                                Toast.makeText(this@BuatakunActivity, responseBody?.message ?: "Gagal registrasi", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@BuatakunActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        // Network or other errors
                        Toast.makeText(this@BuatakunActivity, "Gagal terhubung: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        tologin.setOnClickListener {
            // Start MasukActivity
            val intent = Intent(this, MasukActivity::class.java)
            startActivity(intent)
        }
    }
}
