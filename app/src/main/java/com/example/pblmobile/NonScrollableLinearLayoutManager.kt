package com.example.pblmobile

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

// Custom LinearLayoutManager to disable vertical scrolling
class NonScrollableLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        // Disable vertical scrolling
        return false
    }
}
