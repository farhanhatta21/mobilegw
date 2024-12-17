package com.example.pblmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MasukActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)

        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val toregister = findViewById<TextView>(R.id.textView8)
        val loginButton = findViewById<Button>(R.id.button3)
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val requestBody = mapOf("email" to email, "password" to password)

                val apiService = RetrofitClient.instance
                apiService.loginUser(requestBody).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse?.status == true) {
                                val editor = sharedPreferences.edit()
                                editor.putInt("id_akun", loginResponse.user.id_akun)
                                editor.putString("email", loginResponse.user.email)
                                editor.putString("user_name", loginResponse.user.nama)
                                editor.putString("password", loginResponse.user.password)
                                editor.apply()

                                Log.d("DEBUG_LOGIN", "ID Akun dan Nama Pengguna Disimpan: ${loginResponse.user.id_akun}, ${loginResponse.user.nama}")

                                Toast.makeText(this@MasukActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@MasukActivity, Menuutama::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("DEBUG_LOGIN", "Login Gagal: ${loginResponse?.message}")
                                Toast.makeText(this@MasukActivity, loginResponse?.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("DEBUG_LOGIN", "Kesalahan Server: ${response.errorBody()?.string()}")
                            Toast.makeText(this@MasukActivity, "Terjadi kesalahan. Coba lagi.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.d("DEBUG_LOGIN", "Jaringan Bermasalah: ${t.message}")
                        Toast.makeText(this@MasukActivity, "Jaringan bermasalah. Coba lagi.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }

        toregister.setOnClickListener {
            // Start MasukActivity
            val intent = Intent(this, BuatakunActivity::class.java)
            startActivity(intent)
        }
    }
}
