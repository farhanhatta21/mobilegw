package com.example.pblmobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pblmobile.Models.Laporan

class AdminAdapter(
    private val laporList: MutableList<Laporan>,
    private val onItemEditListener: (Laporan) -> Unit, // Listener for edit
    private val onItemDeleteListener: (Laporan, Int) -> Unit // Listener for delete
) : RecyclerView.Adapter<AdminAdapter.LaporViewHolder>() {

    class LaporViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val ivBukti: ImageView = itemView.findViewById(R.id.ivBukti)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return LaporViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporViewHolder, position: Int) {
        val laporan = laporList[position]
        holder.tvNama.text = laporan.nama
        holder.tvLokasi.text = laporan.lokasi

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(laporan.bukti)
            .placeholder(R.drawable.loading)
            .error(R.drawable.gambartdktersedia)
            .into(holder.ivBukti)

        // Long press event handler
        holder.itemView.setOnLongClickListener {
            val popupMenu = android.widget.PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.menuInflater.inflate(R.menu.menu_admin_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onItemEditListener(laporan) // Call edit listener
                        true
                    }
                    R.id.action_delete -> {
                        onItemDeleteListener(laporan, position) // Call delete listener
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int = laporList.size

    // Method to remove item
    fun removeItem(position: Int) {
        laporList.removeAt(position)
        notifyItemRemoved(position)
    }
}
