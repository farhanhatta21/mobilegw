package com.example.pblmobile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class Menuutama : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menuutama) // Assuming you have this layout
        replaceFragment(Dashboard())

        // Handle bottom navigation item selection
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(Dashboard())
                    true
                }

                R.id.nav_pelaporan -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(LaporActivity())
                    true
                }

                R.id.setting -> {
                    // Replace fragment with ProfileFragment
                    replaceFragment(Setting())
                    true
                }
                else -> false
            }
        }

        // Handle bottom navigation insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_navigation)) { v, insets ->
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