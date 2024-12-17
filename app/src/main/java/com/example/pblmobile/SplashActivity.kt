package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.Api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val appPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val onboardingCompleted = appPreferences.getBoolean("onboarding_completed", false)

        val userPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val isUserLoggedIn = userPreferences.contains("id_akun")

        if (!onboardingCompleted) {
            navigateTo(OnboardingActivity::class.java)
            return
        }

        if (isUserLoggedIn) {
            val idAkun = userPreferences.getInt("id_akun", -1)
            if (idAkun != -1) {
                checkUserRole(idAkun)
            } else {
                navigateTo(MainActivity::class.java)
            }
        } else {
            navigateTo(MainActivity::class.java)
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }

    private fun checkUserRole(idAkun: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getUserData(mapOf("id_akun" to idAkun))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val role = response.body()?.data?.role
                        when (role) {
                            "admin" -> navigateTo(Menuadmin::class.java)
                            "user" -> navigateTo(Menuutama::class.java)
                            else -> {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "Role tidak dikenal",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateTo(MainActivity::class.java)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@SplashActivity,
                            "Gagal mendapatkan data user",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateTo(MainActivity::class.java)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SplashActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    navigateTo(MainActivity::class.java)
                }
            }
        }
    }
}
