package com.example.pblmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pblmobile.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Prepare onboarding pages
        val onboardingPages = listOf(
            createOnboardingFragment(R.drawable.sampahsaya, "Welcome!", "Bantu Kami Melaporkan Sampah² Berserakan ya"),
            createOnboardingFragment(R.drawable.ic_map, "Lokasi Sampah", "Tentukan lokasi sampah yang tepat Pada Pelaporan"),
            createOnboardingFragment(R.drawable.baseline_camera_alt_24, "Ambil Foto Sampah", "Sebagai Bukti Di pelaporan ")
        )

        // Setup ViewPager with the adapter
        onboardingAdapter = OnboardingAdapter(this, onboardingPages)
        binding.viewPager.adapter = onboardingAdapter

        // Handle "Next" button
        binding.buttonNext.setOnClickListener {
            currentPage++
            if (currentPage < onboardingPages.size) {
                binding.viewPager.currentItem = currentPage
            } else {
                // Mark onboarding as completed in SharedPreferences
                markOnboardingCompleted()

                // Go to Main Activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun createOnboardingFragment(imageResId: Int, title: String, description: String): OnboardingFragment {
        val fragment = OnboardingFragment()
        fragment.setData(imageResId, title, description)
        return fragment
    }

    // Mark onboarding as completed using SharedPreferences
    private fun markOnboardingCompleted() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("onboarding_completed", true)
        editor.apply()
    }
}
