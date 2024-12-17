package com.example.pblmobile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class Menuadmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menuadmin) // Assuming you have this layout
        replaceFragment(AdminActivity())

        // Handle bottom navigation item selection
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.admin_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tindakan -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(AdminActivity())
                    true
                }

                R.id.map -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(Adminmap())
                    true
                }
                R.id.berita -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(BeritaFragment())
                    true
                }

                else -> false
            }
        }

        // Handle bottom navigation insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_navigation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to replace the current fragment
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}