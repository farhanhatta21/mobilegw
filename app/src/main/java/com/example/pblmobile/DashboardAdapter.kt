package com.example.pblmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardAdapter(private val items: List<DashboardItem>) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.subtitleTextView.text = item.subtitle
        holder.dateTextView.text = item.date

        // Atur warna berdasarkan subtitle
        val context = holder.itemView.context
        holder.subtitleTextView.setTextColor(
            when (item.subtitle) {
                "Belum Ditanggapi" -> context.getColor(R.color.red)
                "Ditanggapi" -> context.getColor(R.color.yellow)
                "Selesai" -> context.getColor(R.color.green)
                else -> context.getColor(R.color.abu)
            }
        )
    }

    override fun getItemCount() = items.size

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val subtitleTextView: TextView = view.findViewById(R.id.eventSubtitle)
        val dateTextView: TextView = view.findViewById(R.id.eventDate)
    }
}

data class DashboardItem(val title: String, val subtitle: String, val date: String)
