package com.example.pblmobile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LaporPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3 // Tiga tab

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BelumDitanggapiFragment()
            1 -> DitanggapiFragment()
            2 -> SelesaiFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
