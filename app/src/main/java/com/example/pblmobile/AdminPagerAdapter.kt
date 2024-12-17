package com.example.pblmobile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminBelumDitanggapi()
            1 -> AdminDitanggapi()
            2 -> AdminSelesai()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
